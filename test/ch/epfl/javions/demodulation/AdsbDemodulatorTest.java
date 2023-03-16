package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AdsbDemodulatorTest {


    @Test
    void nextMessage() {

    }

    @Test
    void Printrawmessage() throws IOException {
        String f =  getClass().getResource("/samples_20230304_1442.bin").getFile();;
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null)
                System.out.println(m);
        }
        // RawMessage[timeStampNs=235839800, bytes=8D4952999915769CF02089DB69B1]
        // RawMessage[timeStampNs=2804904800, bytes=8D4241A9EA11A898011C08B21C01]
        //
        //


    }
}