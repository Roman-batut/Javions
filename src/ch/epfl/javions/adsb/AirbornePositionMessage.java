package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Record representing an ADS-B airborne position message
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public record AirbornePositionMessage(long timeStampNs,IcaoAddress icaoAddress,double altitude,int parity,double x,double y)
        implements Message{

    private static final double NORMALISATION = Math.scalb(1d, 17);

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
        //if(icaoAddress == null){
        //    throw new NullPointerException();
        //}
        Preconditions.checkNotNull(icaoAddress);

        Preconditions.checkArgument(timeStampNs>=0);
        Preconditions.checkArgument(parity==1 || parity==0);
        Preconditions.checkArgument(0<=x || x<1);
        Preconditions.checkArgument(0<=y || y<1);
    }

    //* Getters
    
    /**
     * Returns the time stamp of the message in nanoseconds
     */
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    /**
     * Returns the ICAO address of the aircraft
     */
    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }


    //* Methods


    /**
     * Returns the airborne position message corresponding to the raw message,
     * or null if the raw message is not an airborne position message
     * @param rawMessage the raw message
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();

        int format = Bits.extractUInt(payload, 34,1);
        double latitude = ((Bits.extractUInt(payload, 17,17))/NORMALISATION);
        double longitude = ((Bits.extractUInt(payload, 0,17))/NORMALISATION);
        int alt  = Bits.extractUInt(payload, 36, 12);

        //Q = 1
        if (Bits.extractUInt(alt, 4, 1) == 1){
           double height = -1000 + (((alt & 0b1111_1110_0000)>>>1 | (alt & 0b0000_0000_1111))*25);
           height = Units.convertFrom(height, Units.Length.FOOT);

           return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), height, format, longitude, latitude);
        }
        //Q = 0
        else {
            //untangling
            int[] val = new int[]{4, 2, 0, 10, 8, 6, 5, 3, 1, 11, 9, 7};
            int untangled = 0b0000_0000_0000;
            for (int i=0 ; i<val.length ; i++) {
                untangled = untangled | ((Bits.extractUInt(alt, val[i], 1)) << (val.length-i-1));
            }

            //decoding
            int strongbyte = greydecode(Bits.extractUInt(untangled,3,9), 9);
            int weakbyte = greydecode(Bits.extractUInt(untangled,0,3),3);
            if(weakbyte==0 || weakbyte==5 || weakbyte==6){
                return null;
            }
            if (weakbyte == 7){
                weakbyte = 5;
            }
            if(strongbyte%2 != 0){
                weakbyte = 6 - weakbyte;
            }

            //height calculation
            double height = -1300 + (weakbyte*100) + (strongbyte*500);
            height = Units.convertFrom(height, Units.Length.FOOT);

            return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), height, format, longitude, latitude);
        }
    }

    //* Private methods

    private static int greydecode(int greycode, int lenght){
        int decoded = 0;
        for (int i=0 ; i<lenght ; i++) {
            decoded = decoded ^ (greycode >>i);
        }

        return decoded;
    }
}