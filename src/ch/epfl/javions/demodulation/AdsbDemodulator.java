package ch.epfl.javions.demodulation;

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
    private long timeStampNs;
    public static int i = 0; //decode

    private int[] tab;
    private int index;

    //* Constructor

    /**
     * Constructor of a demodulator
     * @param samplesStream the stream of samples
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the stream is null
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException{
        window = new PowerWindow(samplesStream, 1200);

        tab = new int[] {sommeP(0), sommeP(1), sommeP(2)};
        index = 0;
    }


    //* Methods
    
    
    /**
     * Returns the next message or null if there is no message left
     * @throws IOException if an I/O error occurs
     */
    public RawMessage nextMessage() throws IOException{

        while(window.isFull()){
            //int previousP = tab[index%3]; //sommeP(0); int P = tab[(index+1)%3]; //sommeP(1); int nextP = tab[(index+2)%3]; // sommeP(2);
            int V = (window.get(6) + window.get(16) + window.get(21) + window.get(26) + window.get(31) + window.get(41));

            window.advance();
            if((tab[index%3] < tab[(index+1)%3]) && (tab[(index+1)%3] > tab[(index+2)%3]) && (tab[(index+1)%3] >= 2*V)) {
                byte[] octs = new byte[RawMessage.LENGTH];
                octs[0] = octAt(0);

                if (RawMessage.size(octs[0]) == RawMessage.LENGTH) {
                    for (int i=1 ; i<RawMessage.LENGTH ; i++) {
                        octs[i] = octAt(i);
                    }

                    timeStampNs = (window.position() * 100);
                    RawMessage v = RawMessage.of(timeStampNs, octs);
                    if (v != null) {
                        window.advanceBy(window.size());
                        i++;
                        return v;
                    }
                }
            }
            index++;
            tab[(index+2)%3] = sommeP(2);
        }

        return null;
    }

    /**
     * Returns the P sum of the 4 samples at the given index
     * @param index the index of the first sample
     */
    private int sommeP(int index){
        return (window.get(index)+window.get(10+index)+window.get(35+index)+window.get(45+index));
    }

    /**
     * Returns the bit at the given index in the window
     * @param index the index of the bit
     */
    private byte bitAt(int index){
        if(window.get(80+10*index) < window.get(85+10*index)){
            return 0;
        }
        return 1;
    }

    /**
     * Returns the byte at the given index in the window
     * @param index the index of the first bit
     */
    private byte octAt(int index) {
        byte oct = 0b00_00_00_00;
        for (int i=0 ; i<8 ; i++) {
            oct = (byte) ((oct << 1) | bitAt(index * 8 + i));
        }

        return oct;
    }

}
