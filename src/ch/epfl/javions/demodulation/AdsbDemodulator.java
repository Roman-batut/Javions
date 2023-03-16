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

    PowerWindow window;

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
        //on vérifie sur index 0 (p-1) index 1 (p) et index 2 (p+1)
        //on calcule v à l'index 1
        //si conditions bonnes alors => décodage
        //sinon on avance d'un (advance()) et on recommence

        //boucle for du flot ?

        while(window.isFull()){
            int previousP = sommeP(0); int P = sommeP(1); int nextP = sommeP(2);
            int V = window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);

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
        //hate de supprimer tout ce code quand on va se rendre compte de son inutilité

    }

    private int sommeP(int index){
        return window.get(0+index)+window.get(10+index)+window.get(35+index)+window.get(45+index);
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
