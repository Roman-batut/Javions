package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AircraftIdentificationMessageTest {

    @Test
    void timeStampNs() {
    }

    @Test
    void icaoAddress() {
    }

    @Test
    void of() throws IOException {
        RawMessage[] message = new RawMessage[384];
        AircraftIdentificationMessage[] mess = new AircraftIdentificationMessage[14];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                message[i] = l;
                if (message[i].typeCode() ==1||message[i].typeCode() ==2||message[i].typeCode() ==3||
                        message[i].typeCode() ==4){
                    mess[k] = AircraftIdentificationMessage.of(message[i]);
                    k++;
                }
            }
            for (int i = 0; i < 14; i++) {
                System.out.println(mess[i]);
            }
            System.out.println(k);

        }
    }

    /* #TODO Rajouter ca dans rendu intermediaire ?
    private static boolean validmessage(long payload) {
        return (RawMessage.typeCode(payload) == 1 || RawMessage.typeCode(payload) == 2
                || RawMessage.typeCode(payload) == 3 || RawMessage.typeCode(payload) == 4);
    }
    hypothese que le type code est bon jsp si on supprime
    */

    @Test
    void category() {
    }

    @Test
    void callSign() {
    }

    @Test
    void TESTDECHAR(){
    }
}
