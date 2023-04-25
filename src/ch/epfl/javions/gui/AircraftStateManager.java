package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HashMap;
import java.util.Map;

public final class AircraftStateManager {

    private Map<AircraftStateAccumulator, IcaoAddress> a = new HashMap<>();

}
