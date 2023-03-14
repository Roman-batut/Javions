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
        //on vérifie sur index 0 (p-1) index 1 (p) et index 2 (p+1)
        //on calcule v à l'index 1
        //si conditions bonnes alors => décodage
        //sinon on avance d'un (advance()) et on recommence

        //boucle for du flot ?

        while (true){
            if(!window.isFull()){
                return null;
            }

            int previousP = sommeP(0); int P = sommeP(1); int nextP = sommeP(2);
            int V = window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);

            if(previousP < P && P > nextP && P >= 2 * V) {
                //décodage

                for (int i=0 ; i<window.size() ; i+=8) {
                    byte oct = 0;
                    for (int j=0 ; j<8 ; j++){
                        oct = (byte) (oct | (bitAt(i+j)<<7-j));
                    }

                    //verification DF type 17


                }

                window.advanceBy(window.size()); //ou longueur du mess
            }
            window.advance();
        }

        //hate de supprimer tout ce code quand on va se rendre compte de son inutilité

    }

    private int sommeP(int index){
        return window.get(0+index)+window.get(10+index)+window.get(35+index)+window.get(45+index);
    }

    private int bitAt(int index){
        if (window.get(80+10*index) < window.get(85+10*index)){
            return 0;
        }
        return 1;
    }

}
