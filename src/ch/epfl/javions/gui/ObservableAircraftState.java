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
import java.util.ArrayList;
import java.util.List;
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
        velocity = new SimpleDoubleProperty(-1);
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

        if(!Double.isNaN(getAltitude())){
            trajectory.add(new AirbornPos(getAltitude(), position));
        }
        this.position.set(position);
    }
    @Override
    public void setAltitude(double altitude) {
        int index = trajectory.size()-1;

        if(getPosition() != null){
            if(trajectory.isEmpty()){
                trajectory.add(new AirbornPos(altitude, getPosition()));
            } else if (oldMessageTimestampNs == getTimeStampNs()){
                trajectory.add(index, new AirbornPos(altitude, getPosition()));
            }
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
    public ReadOnlyObjectProperty<CallSign> callSignProperty(){ return callSign;}
    public ReadOnlyObjectProperty<GeoPos> positionProperty(){ return position;}
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
    public AircraftData getAircraftData(){ return aircraftData; }

    //* Getters fixed infos

    public AircraftRegistration getRegistration(){

        return aircraftData.registration();
    }
    public AircraftTypeDesignator getTypeDesignator(){

        return aircraftData.typeDesignator();
    }
    public String getModel(){
        return aircraftData.model();
    }
    public AircraftDescription getDescription(){
        return aircraftData.description() ;
    }
    public WakeTurbulenceCategory getWakeTurbulenceCategory(){
        return aircraftData.wakeTurbulenceCategory();
    }

    public record AirbornPos(double altitude, GeoPos position){
        public AirbornPos(double altitude, GeoPos position){
            this.altitude = altitude;
            this.position = position;
        }
    }


}