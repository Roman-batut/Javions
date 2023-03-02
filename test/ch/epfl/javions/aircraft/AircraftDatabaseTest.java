package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

//0083CC,ZS-ALF,E135,EMBRAER ERJ-135,L2J,M

class AircraftDatabaseTest {
    private  AircraftData ref = new AircraftData (new AircraftRegistration("ZS-ALF"),new AircraftTypeDesignator("E135"),
            "EMBRAER ERJ-135" , new AircraftDescription("L2J"), WakeTurbulenceCategory.of("M"));
     String directory = getClass().getResource("/aircraft.zip").getFile();
    private AircraftDatabase tocompare = new AircraftDatabase(directory);

    @Test
    void CheckIfFileFound () throws IOException{
        AircraftData todo = tocompare.get(new IcaoAddress("0083CC"));
        assertEquals(ref.registration().string(), todo.registration().string());
        assertEquals(ref.typeDesignator().string(), todo.typeDesignator().string());
        assertEquals(ref.model(), todo.model());
        assertEquals(ref.description().string(), todo.description().string());
        assertEquals(ref.wakeTurbulenceCategory(), todo.wakeTurbulenceCategory());
    }

    @Test
    void Chexnotokfile() throws IOException{

        assertEquals(null, tocompare.get(new IcaoAddress("000000")));

    }

    @Test
    void checkwrongdirectory(){
        assertDoesNotThrow(() -> new AircraftDatabase(directory));
        assertThrows(NullPointerException.class, () -> new AircraftDatabase(null));
    }

}