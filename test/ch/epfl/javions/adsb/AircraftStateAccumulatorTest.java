package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import static org.junit.jupiter.api.Assertions.*;

class AircraftStateAccumulatorTest {

    @Test
    void stateSetter() {
    }

    @Test
    void update() {
    }

    @Test
    void main() throws IOException {
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        try (InputStream s = new FileInputStream(directory)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a =
                    new AircraftStateAccumulator<>(new AircraftState());
            while ((m = d.nextMessage()) != null) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;

                Message pm = MessageParser.parse(m);
                if (pm != null) a.update(pm);
            }
        }
    }
}