package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *  Class representing an aircraft database
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public class AircraftDatabase {

    private final String fileName;

    //* Constants

    private static final String CSV = ".csv";
    private static final String COMA = ",";

    //* Constructor

    /**
     *  Constructor of an aircraft database
     *  @param fileName the name of the file containing the aircraft database
     *  @throws NullPointerException if the file name is null
     */
    public AircraftDatabase(String fileName){
        this.fileName = Objects.requireNonNull(fileName);
    }


    //* Methods

    /**
     *  Gets the aircraft data corresponding to the given ICAO address
     *  @param address the ICAO address
     *  @throws IOException if an I/O error occurs
     *  @throws NullPointerException if the ICAO address is null
     *  @return the aircraft data corresponding to the given ICAO address or null if the ICAO address is not found
     */
    public AircraftData get(IcaoAddress address) throws IOException{
        String sAddress = address.string();
        String file = sAddress.substring(sAddress.length() - 2) + CSV;

        try (
                ZipFile zipFile = new ZipFile(fileName);
                InputStream stream = zipFile.getInputStream(zipFile.getEntry(file));
                Reader reader = new InputStreamReader(stream, UTF_8);
                BufferedReader buffer = new BufferedReader(reader))
        {
            String[] infos;
            String line;
            while ((line = buffer.readLine()) != null) {
                if (line.startsWith(sAddress)) {
                    infos = line.split(COMA, -1);

                    return new AircraftData(
                            new AircraftRegistration(infos[1]),
                            new AircraftTypeDesignator(infos[2]), infos[3],
                            new AircraftDescription(infos[4]),
                            WakeTurbulenceCategory.of(infos[5]));
                }

                if(line.compareTo(sAddress) >= 0){
                    return null;
                }
            }

            return null;
        }
    }
}
