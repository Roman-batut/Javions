package ch.epfl.javions.aircraft;


import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTest {

    @Test
    void IcaoAddressConstraints() {
        Pattern pattern = Pattern.compile("[0-9A-F]{6}");
        System.out.println(pattern.matcher("").matches());
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B1814J"));
        assertDoesNotThrow(() -> new IcaoAddress("4B1814"));
    }


    @Test
    void AircraftRegistrationConstraints() {
        Pattern pattern = Pattern.compile("[A-Z0-9 .?/_+-]+");
        System.out.println(pattern.matcher("").matches());
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("HB-JDC^"));
        assertDoesNotThrow(() -> new AircraftRegistration("HB-JDC"));
    }

    @Test
    void AircraftTypeDesignatorConstraints() {
        Pattern pattern = Pattern.compile("[A-Z0-9]{2,4}");
        System.out.println(pattern.matcher("").matches());
        assertDoesNotThrow(() -> new AircraftTypeDesignator(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("abh"));
    }

    @Test
    void AircraftDescriptionConstraints() {
        Pattern pattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
        System.out.println(pattern.matcher("").matches());
        assertDoesNotThrow(() -> new AircraftDescription(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("ABJ"));
    }
}
