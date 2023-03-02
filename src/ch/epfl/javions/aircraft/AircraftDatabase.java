package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AircraftDatabase {

    private String filename;

    public AircraftDatabase(String fileName) {
        if (fileName == null) {
            throw new NullPointerException();
        }
        this.filename = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException {

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
