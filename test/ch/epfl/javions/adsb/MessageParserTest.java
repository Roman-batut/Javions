package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MessageParserTest {

    @Test
    void parse() throws IOException {

        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        int vel = 0;
        int pos = 0;
        int id = 0;
        try (InputStream s = new FileInputStream(directory)) {
            RawMessage m;
            AdsbDemodulator d = new AdsbDemodulator(s);
            while ((m = d.nextMessage()) != null) {
                Message mes = MessageParser.parse(m);
                if (mes == null) {
                    continue;
                }
                System.out.println(mes);
                switch (mes) {
                    case AircraftIdentificationMessage idm -> {
                        id++;
                    }
                    case AirborneVelocityMessage velm -> {
                        vel++;
                    }
                    case AirbornePositionMessage posm -> {
                        pos++;
                    }
                    default -> System.out.println("pas possicle");
                }

            }
            System.out.println(pos + " " + vel + " " + id);
        }
    }
}