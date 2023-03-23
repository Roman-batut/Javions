package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirbornePositionMessageTest {

    String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream s = new FileInputStream(directory))

    {
        AdsbDemodulator d = new AdsbDemodulator(s);
    }
    private RawMessage message1 = AdsbDemodulator

    @Test
    void timeStampNs() {
    }

    @Test
    void icaoAddress() {
    }

    @Test
    void of() {
    }

    @Test
    void altitude() {
    }

    @Test
    void parity() {
    }

    @Test
    void x() {
    }

    @Test
    void y() {
    }
}