package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *  Class representing an aircraft database
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public class AircraftDatabase {

    private String filename;

    //* Constructor

    /**
     *  Constructor of an aircraft database
     *  @param fileName the name of the file containing the aircraft database
     *  @throws NullPointerException if the file name is null
     */
    public AircraftDatabase(String fileName){
        if (fileName == null) {
            throw new NullPointerException();
        }

        this.filename = fileName;
    }


    //* Methods


    /**
     *  Returns the aircraft data corresponding to the given ICAO address or null if the ICAO address is not found
     *  @param address the ICAO address
     *  @throws IOException if an I/O error occurs
     *  @throws NullPointerException if the ICAO address is null
     */
    public AircraftData get(IcaoAddress address) throws IOException{

        String sAddress = address.string();
        String file = sAddress.substring(sAddress.length() - 2) + ".csv";

        try (
                ZipFile zipFile = new ZipFile(filename);
                InputStream stream = zipFile.getInputStream(zipFile.getEntry(file));
                Reader reader = new InputStreamReader(stream, UTF_8);
                BufferedReader buffer = new BufferedReader(reader);)
        {
            String[] infos;
            String line;
            boolean compare = true;
            while ((line = buffer.readLine()) != null && compare) {
                if (line.startsWith(sAddress)) {
                    infos = line.split(",", -1);
                    AircraftData data = new AircraftData(
                            new AircraftRegistration(infos[1]),
                            new AircraftTypeDesignator(infos[2]), infos[3],
                            new AircraftDescription(infos[4]),
                            WakeTurbulenceCategory.of(infos[5]));

                    return data;
                }

                compare = line.compareTo(sAddress) <= 0;
            }

            return null;
        }
    }
}
