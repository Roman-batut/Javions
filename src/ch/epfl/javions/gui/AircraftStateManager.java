package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  Class representing a manager of aircraft states
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class AircraftStateManager {

    private final Map<IcaoAddress,AircraftStateAccumulator<ObservableAircraftState>> stateManager;
    private final ObservableSet<ObservableAircraftState> observableState;
    private final ObservableSet<ObservableAircraftState> observableStateunmodif;
    private final AircraftDatabase aircraftDatabase;
    private long timeStampNs;

    //* Constants

    private final long MINUTE_IN_NS = 10000000000L;

    //* Constructor

    /**
     * AircraftStateManager's constructor
     * @param aircraftDatabase the aircraft database
     */
    public AircraftStateManager(AircraftDatabase aircraftDatabase){
        this.aircraftDatabase = aircraftDatabase;
        stateManager = new HashMap<>();
        observableState = FXCollections.observableSet();
        observableStateunmodif = FXCollections.unmodifiableObservableSet(observableState);
    }

    //* Getters
   
    /**
     * Gets the observable set of aircraft states
     * @return the observable set of aircraft states
     */
    public ObservableSet<ObservableAircraftState> states(){
        return observableStateunmodif;
    }


    //* Methods

    /**
     * Updates the aircraft state manager with the message
     * @param message the message
     * @throws IOException if the message is null
     */
    public void updateWithMessage(Message message) throws IOException{
        IcaoAddress icao = message.icaoAddress();
        timeStampNs = message.timeStampNs();

        if(stateManager.get(icao) == null){
            stateManager.put(icao, new AircraftStateAccumulator<>(new ObservableAircraftState(icao,aircraftDatabase)));
        }

        stateManager.get(icao).update(message);

        if(stateManager.get(icao).stateSetter().getPosition() != null){
            observableState.add(stateManager.get(icao).stateSetter());
        }
    }

    /**
     * Purges the aircraft state manager
     */
    public void purge(){
        observableState.removeIf(
                state -> (timeStampNs - state.getTimeStampNs()) >= MINUTE_IN_NS);

        stateManager.entrySet().removeIf(
                entry -> (timeStampNs - entry.getValue().stateSetter().getTimeStampNs()) >= MINUTE_IN_NS);
    }
}
