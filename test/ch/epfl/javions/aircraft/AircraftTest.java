package ch.epfl.javions.aircraft;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTest {

    @Test
    void IcaoAddressConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B1814J"));
        assertDoesNotThrow(() -> new IcaoAddress("4B1814"));
    }


    @Test
    void AircraftRegistrationConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("HB-JDC^"));
        assertDoesNotThrow(() -> new AircraftRegistration("HB-JDC"));
    }

    @Test
    void AircraftTypeDesignatorConstraints() {
        assertDoesNotThrow(() -> new AircraftTypeDesignator(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("abh"));
    }

    @Test
    void AircraftDescriptionConstraints() {
        assertDoesNotThrow(() -> new AircraftDescription(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("ABJ"));
    }
}
