package ch.epfl.javions.adsb;
import ch.epfl.javions.GeoPos;

public interface AircraftStateSetter {

    //change Aircraft TimeStamp to the given value
    void setLastMessageTimeStampNs(long timeStampNs);

    //change Aircraft category to the given value
    void setCategory(int category);

    //change Aircraft callSign to the given value
    void setCallSign(CallSign callSign);

    //change Aircraft position to the given value
    void setPosition(GeoPos position);

    //change Aircraft altitude to the given value
    void setAltitude(double altitude);

    //change Aircraft velocity to the given value
    void setVelocity(double velocity);

    //change Aircraft direction to the given value
    void setTrackOrHeading(double trackOrHeading);
}
