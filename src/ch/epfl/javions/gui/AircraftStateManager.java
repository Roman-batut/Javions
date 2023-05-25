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


public final class AircraftStateManager {

    private final long MINUTE_IN_NS = 10000000000L;
    private Map<IcaoAddress,AircraftStateAccumulator<ObservableAircraftState>> stateManager;
    private ObservableSet<ObservableAircraftState> observableState;
    private ObservableSet<ObservableAircraftState> observableStateunmodif;
    private AircraftDatabase aircraftDatabase;
    private long timeStampNs;

    public AircraftStateManager(AircraftDatabase aircraftDatabase){
        this.aircraftDatabase = aircraftDatabase;
        stateManager = new HashMap<>();
        observableState = FXCollections.observableSet();
        observableStateunmodif = FXCollections.unmodifiableObservableSet(observableState);
    }

    public ObservableSet<ObservableAircraftState> states(){
        return observableStateunmodif;
    }

    public void updateWithMessage(Message message) throws IOException{
        IcaoAddress icao = message.icaoAddress();
        timeStampNs = message.timeStampNs();
        if(stateManager.get(icao) == null){
            stateManager.put(icao, new AircraftStateAccumulator<>(new ObservableAircraftState(icao,aircraftDatabase)));
        }else {
            stateManager.get(icao).update(message);
        }
        if(stateManager.get(icao).stateSetter().getPosition() != null){
            observableState.add(stateManager.get(icao).stateSetter());
        }

    }

    public void purge(){
        observableState.removeIf(state -> (timeStampNs - state.getTimeStampNs()) >= MINUTE_IN_NS);
        stateManager.entrySet().removeIf(entry -> (timeStampNs - entry.getValue().stateSetter().getTimeStampNs()) >= MINUTE_IN_NS);
    }
}
