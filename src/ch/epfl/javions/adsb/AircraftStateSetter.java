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
     * @param timeStampNs the new time stamp
    */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Changes Aircraft category to the given value
     * @param category the new category
     */
    void setCategory(int category);

    /**
     * Changes Aircraft callsign to the given value
     * @param callSign the new callsign
     */
    void setCallSign(CallSign callSign);

    /**
     * Changes Aircraft position to the given value
     * @param position the new position
     */
    void setPosition(GeoPos position);

    /**
     * Changes Aircraft altitude to the given value
     * @param altitude the new altitude
     */
    void setAltitude(double altitude);

    /**
     * Changes Aircraft velocity to the given value
     * @param velocity the new velocity
     */
    void setVelocity(double velocity);

    /**
     * Changes Aircraft track or heading to the given value
     * @param trackOrHeading the new track or heading
     */
    void setTrackOrHeading(double trackOrHeading);
}
