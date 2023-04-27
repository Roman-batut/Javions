package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.collections.ObservableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.input.EOFException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ObservableAircraftStateTest {
    @Test
    void testDisplayMessages() throws IOException {
//        final char[] directions = new char[]{'↑', '↗️', '→', '↘️', '↓', '↙️', '←', '↖️'};
        var directory = getClass().getResource("/messages_20230318_0915.bin").getFile();
        var directory2 = getClass().getResource("/aircraft.zip").getFile();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(directory)))) {
            AircraftStateManager manager = new AircraftStateManager(new AircraftDatabase(directory2));
            byte[] bytes = new byte[RawMessage.LENGTH];
            int i = 0;
            while (i <= 1000) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                RawMessage rawMsg = RawMessage.of(timeStampNs, bytes);
                Message msg = MessageParser.parse(rawMsg);
                i++;
                if (msg == null) {
                    continue;
                }
                manager.updateWithMessage(msg);
                manager.purge();
                printAllAircraftStates(manager.states());
            }
            //printAllAircraftStates(manager.states());

        } catch (EOFException e) { /* nothing to do */ }
    }

    public static void printAllAircraftStates(ObservableSet<ObservableAircraftState> rawStates) {
        System.out.print("\033[H\033[2J");
        List<ObservableAircraftState> states = new java.util.ArrayList<>(rawStates.stream().toList());//.sort(AddressComparator.compare());
        states.sort(Comparator.comparing(s -> s.getIcaoAddress().string()));
        System.out.printf("%-10s | %-12s | %-12s | %-20s | %-20s | %-20s | %-12s | %-10s\n",
                "ICAO",
                "Call Sign",
                "Registration",
                "Model",
                "Longitude",
                "Latitude",
                "Altitude",
                "Speed");
        System.out.println("_________________________________________________________________________________________________________________________________________________________________________________");
        for (ObservableAircraftState state : states) {

            System.out.printf("%10s | %12.12s | %12.12s | %20.20s | %19.12f° | %19.12f° |  %11.12s | %10.15s\n",
                    state.getIcaoAddress().string(),
                    state.getCallSign() == null ? "" : state.getCallSign().string(),
                    state.getRegistration() == null ? "" : state.getRegistration().string(),
                    state.getModel() == null ? "" : state.getModel(),
                    Units.convert(state.getPosition().longitude(), Units.Angle.RADIAN, Units.Angle.DEGREE),
                    Units.convert(state.getPosition().latitude(), Units.Angle.RADIAN, Units.Angle.DEGREE),
                    (int) state.getAltitude(),
                    state.getVelocity() == 0 ? "" : (int) (Units.convertTo(state.getVelocity(), Units.Speed.KILOMETER_PER_HOUR)));
        }
    }
}