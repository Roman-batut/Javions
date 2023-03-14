package ch.epfl.javions.adsb;
import ch.epfl.javions.GeoPos;

/**
 *  Interface representing a setter for an AircraftState
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public interface AircraftStateSetter {

    /**
     * Changes Aircraft TimeStamp to the given value
     * @param timeStampNs 
    */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Changes Aircraft category to the given value
     * @param category
     */
    void setCategory(int category);

    /**
     * Changes Aircraft callsign to the given value
     * @param callSign
     */
    void setCallSign(CallSign callSign);

    /**
     * Changes Aircraft position to the given value
     * @param position
     */
    void setPosition(GeoPos position);

    /**
     * Changes Aircraft altitude to the given value
     * @param altitude
     */
    void setAltitude(double altitude);

    /**
     * Changes Aircraft velocity to the given value
     * @param velocity
     */
    void setVelocity(double velocity);

    /**
     * Changes Aircraft track or heading to the given value
     * @param trackOrHeading
     */
    void setTrackOrHeading(double trackOrHeading);
}
