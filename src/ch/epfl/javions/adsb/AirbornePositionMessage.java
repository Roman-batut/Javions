package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Record representing an ADS-B airborne position message
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public record AirbornePositionMessage(long timeStampNs,IcaoAddress icaoAddress,double altitude,int parity,double x,double y) implements Message{

    private static final double REGUL = 1 << 17;

    private static final int FORMAT_START = 34;
    private static final int FORMAT_SIZE = 1;

    private static final int LATITUDE_START = 17;
    private static final int LATITUDE_SIZE = 17;
    private static final int LONGITUDE_START = 0;
    private static final int LONGITUDE_SIZE = 17;

    private static final int ALT_START = 36;
    private static final int ALT_SIZE = 12;
    private static final int Q_BIT = 4;

    private static final int STRONGBYTE_START = 3;
    private static final int STRONGBYTE_SIZE = 9;
    private static final int WEAKBYTE_START = 0;
    private static final int WEAKBYTE_SIZE = 3;

    //* Constructor

    /**
     * Constructor of an airborne position message
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param icaoAddress the ICAO address of the aircraft
     * @param altitude the altitude of the aircraft
     * @param parity the parity of the message
     * @param x the normalised longitude coordinate of the aircraft
     * @param y the normalised latitude coordinate of the aircraft
     * @throws NullPointerException if the ICAO address is null
     * @throws IllegalArgumentException if the time stamp is negative or the parity is not 0 or 1 or the x or y coordinates are not normalised
     */
    public AirbornePositionMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(parity == 1 || parity == 0);
        Preconditions.checkArgument(0 <= x & x < 1);
        Preconditions.checkArgument(0 <= y & y < 1);
    }


    //* Methods

    /**
     * Decode an airbone position message
     * @param rawMessage the raw message
     * @returns the airborne position message corresponding to the raw message,
     * or null if the raw message is not an airborne position message
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();

        int format = Bits.extractUInt(payload, FORMAT_START, FORMAT_SIZE);
        double latitude = ((Bits.extractUInt(payload, LATITUDE_START, LATITUDE_SIZE)) / REGUL);
        double longitude = ((Bits.extractUInt(payload, LONGITUDE_START, LONGITUDE_SIZE)) / REGUL);
        int alt  = Bits.extractUInt(payload, ALT_START, ALT_SIZE);

        //Q = 1
        if (Bits.testBit(alt, Q_BIT)){
           double height = (-1000 + (((alt & 0b1111_1110_0000) >>> 1 | (alt & 0b0000_0000_1111)) * 25));
           height = Units.convertFrom(height, Units.Length.FOOT);

           return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), height, format, longitude, latitude);
        }
        //Q = 0
        else {
            //untangling
            int[] val = new int[]{4, 2, 0, 10, 8, 6, 5, 3, 1, 11, 9, 7};
            int untangled = 0b0000_0000_0000;
            for (int i=0 ; i<val.length ; i++) {
                untangled = untangled | ((Bits.extractUInt(alt, val[i], 1)) << (val.length - i - 1));
            }

            //decoding
            int strongbyte = greydecode(Bits.extractUInt(untangled, STRONGBYTE_START, STRONGBYTE_SIZE), STRONGBYTE_SIZE);
            int weakbyte = greydecode(Bits.extractUInt(untangled, WEAKBYTE_START, WEAKBYTE_SIZE),WEAKBYTE_SIZE);
            switch (weakbyte){
                case 0, 5, 6 -> {
                    return null;
                }
                case 7 -> weakbyte = 5;
            }
            if(strongbyte % 2 != 0){
                weakbyte = 6 - weakbyte;
            }

            //height calculation
            double height = (-1300 + (weakbyte * 100) + (strongbyte * 500));
            height = Units.convertFrom(height, Units.Length.FOOT);

            return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), height, format, longitude, latitude);
        }
    }

    //* Private methods

    private static int greydecode(int greycode, int lenght){
        int decoded = 0;
        for (int i=0 ; i<lenght ; i++){
            decoded = decoded ^ (greycode >> i);
        }

        return decoded;
    }
}
