package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Record representing an ADS-B airborne velocity message
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public record AirborneVelocityMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double speed,
                                      double trackOrHeading) implements Message{

    //* Constants
    
    private static final double HDG_REGUL = (1 << 10);
    private static final int VELOCITY_REGUL = 1;

    private static final int SUBTYPE_START = 48;
    private static final int SUBTYPE_SIZE = 3;

    private static final int INFOS_START = 21;
    private static final int INFOS_SIZE = 22;

    private static final int DNS_START = 10;
    private static final int DNS_SIZE = 1;
    private static final int VNS_START = 0;
    private static final int VNS_SIZE = 10;
    private static final int DEW_START = 21;
    private static final int DEW_SIZE = 1;
    private static final int VEW_START = 11;
    private static final int VEW_SIZE = 10;

    private static final int HDG_START = 11;
    private static final int HDG_SIZE = 10;
    private static final int SH_START = 21;
    private static final int SH_SIZE = 1;
    private static final int AS_SIZE = 10;
    private static final int AS_START = 0;

    //* Constructor

    /**
     * Constructor of an airborne velocity message
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param icaoAddress the ICAO address of the aircraft
     * @param speed the speed of the aircraft
     * @param trackOrHeading the track or heading of the aircraft
     * @throws NullPointerException if the ICAO address is null
     * @throws IllegalArgumentException if the time stamp is negative or the speed or track or heading is negative
     */
    public AirborneVelocityMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(speed >= 0 && trackOrHeading >= 0 && timeStampNs >= 0);
    }


    //* Methods

    /**
     * Returns the airborne velocity message corresponding to the raw message, 
     * or null if one of the raw message's fields is invalid
     * @param rawMessage the raw message
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int subType = Bits.extractUInt(payload, SUBTYPE_START, SUBTYPE_SIZE);
        long infos = Bits.extractUInt(payload, INFOS_START, INFOS_SIZE);

        if(subType == 1 || subType == 2){
            int Vns = Bits.extractUInt(infos, VNS_START,VNS_SIZE) - VELOCITY_REGUL;
            int Dns = Bits.extractUInt(infos, DNS_START, DNS_SIZE);
            int Vew = Bits.extractUInt(infos, VEW_START, VEW_SIZE) - VELOCITY_REGUL;
            int Dew = Bits.extractUInt(infos, DEW_START, DEW_SIZE);
            if(Vns < 0 || Vew < 0){
                return null;
            }

            int Vy = speedcalculator(subType, Vns);
            int Vx = speedcalculator(subType, Vew);
            double speed = Units.convertFrom(Math.hypot(Vy, Vx), Units.Speed.KNOT);

            if(Dns == 1) {Vy = -Vy;}
            if(Dew == 1) {Vx = -Vx;}

            double angle = Math.atan2(Vx, Vy);
            if(angle < 0){
                angle += Units.Angle.TURN;
            }

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);

        } else if(subType == 3 || subType == 4){
            int as = Bits.extractUInt(infos, AS_START,AS_SIZE) - VELOCITY_REGUL;
            int Hdg = Bits.extractUInt(infos, HDG_START, HDG_SIZE);
            int sh = Bits.extractUInt(infos, SH_START, SH_SIZE);
            if(as < 0 || sh == 0){
                return null;
            }

            double speed = Units.convertFrom(speedcalculator(subType - 2, as), Units.Speed.KNOT);
            double angle = Units.convert(Hdg / HDG_REGUL, Units.Angle.TURN, Units.Angle.RADIAN);

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);
        } else {
             return null;
        }
    }

    //* Private methods

    /**
     * Calculates the speed of the aircraft
     * @param subtype the subtype of the message
     * @param speed the speed of the aircraft
     * @return the speed of the aircraft
     */
    private static int speedcalculator (int subtype, int speed){
        return (int)(speed * Math.pow(subtype, 2));
    }
}
