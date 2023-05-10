package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.Objects;

public final class ObservableAircraftState implements AircraftStateSetter {

    private final AircraftData aircraftData;

    private AircraftStateAccumulator accumulator;

    private LongProperty timeStampNs;
    private long oldMessageTimestampNs;
    private IntegerProperty category;
    private SimpleObjectProperty<CallSign> callSign;
    private SimpleObjectProperty<GeoPos> position;
    private DoubleProperty altitude;
    private DoubleProperty velocity;
    private DoubleProperty trackOrHeading;
    private ObservableList<AirbornPos> trajectory;
    private ObservableList<AirbornPos> trajectoryunmodifiable;
    private IcaoAddress icaoAddress;


    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftDatabase aircraftDatabase) throws IOException {
        this.icaoAddress = icaoAddress;
        aircraftData = aircraftDatabase.get(icaoAddress);
        accumulator = new AircraftStateAccumulator<>(this);
        trajectory = FXCollections.observableArrayList();
        trajectoryunmodifiable = FXCollections.unmodifiableObservableList(trajectory);

        timeStampNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty();
        velocity = new SimpleDoubleProperty();
        trackOrHeading = new SimpleDoubleProperty();
    }

    //* Setters

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        this.timeStampNs.set(timeStampNs);
    }
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }
    @Override
    public void setPosition(GeoPos position) {
        int index = trajectory.size()-1;
        if(trajectory.isEmpty() || !(position.equals(trajectory.get(index).position))){
            trajectory.add(new AirbornPos(getAltitude(), position));
            oldMessageTimestampNs = (getTimeStampNs());
        }else if(oldMessageTimestampNs == getTimeStampNs()){
            trajectory.set(index, new AirbornPos(getAltitude(), position));
        }
        this.position.set(position);

    }
    @Override
    public void setAltitude(double altitude) {
        int index = trajectory.size()-1;
        if(trajectory.isEmpty() || !(position.equals(trajectory.get(index).position))){
            trajectory.add(new AirbornPos(altitude, getPosition()));
            oldMessageTimestampNs = getTimeStampNs();
        }else if(oldMessageTimestampNs == getTimeStampNs()){
            trajectory.set(index, new AirbornPos(altitude, getPosition()));
        }

        this.altitude.set(altitude);

    }
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    //* View Getters
    public ReadOnlyLongProperty timeStampNs(){ return timeStampNs; }
    public ReadOnlyIntegerProperty categoryProperty(){ return category; }
    public ReadOnlyDoubleProperty altitudeProperty(){ return altitude; }
    public ReadOnlyDoubleProperty velocityProperty(){ return velocity; }
    public ReadOnlyDoubleProperty trackOrHeading(){ return trackOrHeading; }
    public ReadOnlyObjectProperty callSignProperty(){ return callSign;}
    public ReadOnlyObjectProperty positionProperty(){ return position;}
    public ObservableList<AirbornPos> trajectoryProperty(){ return trajectoryunmodifiable; }

    //* Getters

    public long getTimeStampNs(){
        return timeStampNs.get();
    }
    public int getCategory(){
        return category.get();
    }
    public double getAltitude(){
        return altitude.get();
    }
    public double getVelocity(){
        return velocity.get();
    }
    public double getTrackOrHeanding(){
        return trackOrHeading.get();
    }
    public CallSign getCallSign(){
        return callSign.get();
    }
    public IcaoAddress getIcaoAddress(){return icaoAddress;}
    public GeoPos getPosition(){
        return position.get();
    }
    public AircraftData getAircraftData(){return aircraftData;}

    //* Getters fixed infos

    public AircraftRegistration getRegistration(){

        return !Objects.isNull(aircraftData) ? aircraftData.registration()  : null;
    }
    public AircraftTypeDesignator getTypeDesignator(){

        return !Objects.isNull(aircraftData) ? aircraftData.typeDesignator() : null;
    }
    public String getModel(){
        return !Objects.isNull(aircraftData) ? aircraftData.model() : null;
    }
    public AircraftDescription getDescription(){
        return !Objects.isNull(aircraftData) ? aircraftData.description() : null;
    }
    public WakeTurbulenceCategory getWakeTurbulenceCategory(){
        return !Objects.isNull(aircraftData) ? aircraftData.wakeTurbulenceCategory() : null;
    }

    public record AirbornPos(double altitude, GeoPos position){
        public AirbornPos(double altitude, GeoPos position){
            this.altitude = altitude;
            this.position = position;
        }
    }


}