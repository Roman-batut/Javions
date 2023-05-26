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

/**
 * Class representing an observable aircraft state
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    private final AircraftData aircraftData;

    private final LongProperty timeStampNs;
    private long oldMessageTimestampNs;
    private final IntegerProperty category;
    private final SimpleObjectProperty<CallSign> callSign;
    private final SimpleObjectProperty<GeoPos> position;
    private final DoubleProperty altitude;
    private final DoubleProperty velocity;
    private final DoubleProperty trackOrHeading;
    private final ObservableList<AirbornPos> trajectory;
    private final ObservableList<AirbornPos> trajectoryunmodifiable;
    private final IcaoAddress icaoAddress;

    //* Constructor

    /**
     * ObservableAircraftState's constructor
     * @param icaoAddress the icao address
     * @param aircraftDatabase the aircraft database
     * @throws IOException if the icao address is null
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftDatabase aircraftDatabase) throws IOException {
        this.icaoAddress = icaoAddress;
        aircraftData = aircraftDatabase.get(icaoAddress);
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

    /**
     * Sets the timestamp
     * @param timeStampNs the new timestamp
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        oldMessageTimestampNs = this.getTimeStampNs();
        this.timeStampNs.set(timeStampNs); }
    
    /**
     * Sets the category
     * @param category the new category
     */
    @Override
    public void setCategory(int category) { this.category.set(category); }
    
    /**
     * Sets the call sign
     * @param callSign the new call sign
     */
    @Override
    public void setCallSign(CallSign callSign) { this.callSign.set(callSign); }
    
    /**
     * Sets the position and checks if it is valid
     * @param position the new position
     */
    @Override
    public void setPosition(GeoPos position) {
        if(!Double.isNaN(getAltitude())) {
            trajectory.add(new AirbornPos(getAltitude(), position));
        }
        this.position.set(position);
    }

    /**
     * Sets the altitude and checks if it is valid
     * @param altitude the new altitude
     */
    @Override
    public void setAltitude(double altitude) {
        int index = trajectory.size() - 1;

        if(getPosition() != null){
            if(trajectory.isEmpty()){
                trajectory.add(new AirbornPos(altitude, getPosition()));
            } else if (oldMessageTimestampNs == getTimeStampNs()){
                trajectory.add(index, new AirbornPos(altitude, getPosition()));
            }
        }
        this.altitude.set(altitude);
    }

    /**
     * Sets the velocity
     * @param velocity the new velocity
     */
    @Override
    public void setVelocity(double velocity) { this.velocity.set(velocity); }

    /**
     * Sets the track or heading
     * @param trackOrHeading the new track or heading
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) { this.trackOrHeading.set(trackOrHeading); }

    //* View Getters

    /**
     * View getter for the timestamp
     * @return the timestamp
     */
    public ReadOnlyLongProperty timeStampNs(){ return timeStampNs; }

    /**
     * View getter for the category
     * @return the category
     */
    public ReadOnlyIntegerProperty categoryProperty(){ return category; }

    /**
     * View getter for the altitude
     * @return the altitude
     */
    public ReadOnlyDoubleProperty altitudeProperty(){ return altitude; }

    /**
     * View getter for the velocity
     * @return the velocity
     */
    public ReadOnlyDoubleProperty velocityProperty(){ return velocity; }

    /**
     * View getter for the track or heading
     * @return the track or heading
     */
    public ReadOnlyDoubleProperty trackOrHeading(){ return trackOrHeading; }

    /**
     * View getter for the call sign
     * @return the call sign
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty(){ return callSign;}

    /**
     * View getter for the position
     * @return the position
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty(){ return position;}

    /**
     * View getter for the trajectory
     * @return the trajectory
     */
    public ObservableList<AirbornPos> trajectoryProperty(){ return trajectoryunmodifiable; }

    //* Getters

    /**
     * Getter for the timestamp
     * @return the timestamp
     */
    public long getTimeStampNs(){ return timeStampNs.get(); }

    /**
     * Getter for the category
     * @return the category
     */
    public int getCategory(){ return category.get(); }

    /**
     * Getter for the altitude
     * @return the altitude
     */
    public double getAltitude(){ return altitude.get(); }

    /**
     * Getter for the velocity
     * @return the velocity
     */
    public double getVelocity(){ return velocity.get(); }

    /**
     * Getter for the track or heading
     * @return the track or heading
     */
    public double getTrackOrHeanding(){ return trackOrHeading.get(); }

    /**
     * Getter for the call sign
     * @return the call sign
     */
    public CallSign getCallSign(){ return callSign.get(); }

    /**
     * Getter for the position
     * @return the position
     */
    public IcaoAddress getIcaoAddress(){return icaoAddress;}

    /**
     * Getter for the trajectory
     * @return the trajectory
     */
    public GeoPos getPosition(){ return position.get(); }

    /**
     * Getter for the trajectory
     * @return the trajectory
     */
    public AircraftData getAircraftData(){ return aircraftData; }

    //* Getters fixed infos

    /**
     * Fixed info getter for the registration
     * @return the registration
     */
    public AircraftRegistration getRegistration(){ return aircraftData.registration(); }

    /**
     * Fixed info getter for the type designator
     * @return the type designator
     */
    public AircraftTypeDesignator getTypeDesignator(){ return aircraftData.typeDesignator(); }

    /**
     * Fixed info getter for the model
     * @return the model
     */
    public String getModel(){ return aircraftData.model(); }

    /**
     * Fixed info getter for the operator
     * @return the operator
     */
    public AircraftDescription getDescription(){ return aircraftData.description(); } 

    /**
     * Fixed info getter for the wake turbulence category
     * @return the wake turbulence category
     */
    public WakeTurbulenceCategory getWakeTurbulenceCategory(){ return aircraftData.wakeTurbulenceCategory();}


    //* Record

    /**
     * Record representing an airborn position
     * @param altitude the altitude
     * @param position the position
     */
    public record AirbornPos(double altitude, GeoPos position){

        /**
         * AirbornPos's constructor
         * @param altitude the altitude
         * @param position the position
         */
        public AirbornPos(double altitude, GeoPos position){
            this.altitude = altitude;
            this.position = position;
        }
    }
}