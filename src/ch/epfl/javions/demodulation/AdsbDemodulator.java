package ch.epfl.javions.demodulation;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class representing a demodulator for ADS-B messages
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class AdsbDemodulator {

    private PowerWindow window;
    private int timeStampNs;

    //* Constructor

    /**
     * Constructor of a demodulator
     * @param samplesStream the stream of samples
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the stream is null
     */
    AdsbDemodulator(InputStream samplesStream) throws IOException{
        window = new PowerWindow(samplesStream, 1200);
    }


    //* Methods
    
    
    /**
     * Returns the next message or null if there is no message left
     * @throws IOException if an I/O error occurs
     */
    public RawMessage nextMessage() throws IOException{

        while(window.isFull()){
            int previousP = sommeP(0); int P = sommeP(1); int nextP = sommeP(2);
            int V = window.get(6) + window.get(16) + window.get(21) + window.get(26) + window.get(31) + window.get(41);

            if(previousP < P && P > nextP && P >= 2 * V) {
                window.advance();

                byte[] octs = new byte[RawMessage.LENGTH];
                octs[0] = octAt(0);

                for(int i=1 ; i<RawMessage.size(octs[0]) ; i++){
                    octs[i] = octAt(i);
                }

                timeStampNs = (int)(window.position()*100);
                window.advanceBy(window.size());
                return RawMessage.of(timeStampNs, octs);
            }

            window.advance();
        }
        
        return null;
    }

    private int sommeP(int index){
        return window.get(index)+window.get(10+index)+window.get(35+index)+window.get(45+index);
    }

    private byte octAt(int index) {
        byte oct = 0;
        for (int i = 0; i < 8; i++) {
            oct = (byte) (oct | (bitAt(index*8 + i) << 7 - i));
        }

        return oct;
    }

    private int bitAt(int index){
        if(window.get(80+10*index) < window.get(85+10*index)){
            return 0;
        }
        return 1;
    }

}
