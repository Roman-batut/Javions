package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {

    PowerWindow window;
    AdsbDemodulator(InputStream samplesStream) throws IOException{
        window = new PowerWindow(samplesStream, 1200);

    }

    public RawMessage nextMessage() throws IOException{

        int p = window.get(0)+window.get(10)+window.get(35)+window.get(45);
        int v = window.get(5)+window.get(15)+window.get(20)+window.get(25)+window.get(30)+window.get(40);

        if(!window.isFull()){
            return null;
        }
        return null;

    }

}
