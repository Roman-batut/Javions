package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

/**
 *  Record representing a raw message
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;
    private static final byte DF = 0b00_01_00_01;

    //* Constructor

    /**
     *  Constructor of a raw message
     *  @param timeStampNs the time stamp of the message in nanoseconds
     *  @param bytes the bytes of the message
     *  @throws IllegalArgumentException if the time stamp is negative or if the bytes are not of length 14
     */
    public RawMessage{
        Preconditions.checkArgument(!(timeStampNs<0));
        Preconditions.checkArgument(bytes.size()==LENGTH);
    }


    //* Methods


    /**
     *  Returns a raw message or null if the CRC24 of the bytes is not 0
     *  @param timeStampNs the time stamp of the message in nanoseconds
     *  @param bytes the bytes of the message
     */
    public static RawMessage of(long timeStampNs, byte[] bytes){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        if(crc24.crc(bytes) != 0) {
            return null;
        }

        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    /**
     *  Returns the size of the message in bytes,
     *  the constant LENGTH if the DF is 17, 0 otherwise
     *  @param byte0 the first byte of the message
     */
    public static int size(byte byte0){
        byte b = (byte)Bits.extractUInt(byte0,3,5);
        if(b == DF){
            return LENGTH;
        }

        return 0;
    }

    /**
     *  Returns the type code of the message,
     *  the type code of its ME field
     *  @param payload the payload of the message
     */
    public static int typeCode(long payload){
        int typecode = (int)(payload >>> 51);

        return typecode;
    }

    /**
     *  Returns the down link format of the message,
     *  its DF field (the five most significant bits of the first byte)
     */
    public int downLinkFormat(){
        return (int)bytes.bytesInRange(0,1);
    }

    /**
     *  Returns the ICAO address of the aircraft
     */
    public IcaoAddress icaoAddress(){
        long icao = bytes.bytesInRange(1,4);
        String b = HexFormat.of().withUpperCase().toHexDigits(icao,6);

        return new IcaoAddress(b);
    }

    /**
     *  Returns the payload of the message,
     *  it's ME field
     */
    public long payload(){
        long mE = bytes.bytesInRange(4,11);

        return mE;
    }

    /**
     *  Returns the type code of the message,
     *  the five most significant bits of its ME field
     */
    public int typeCode(){
        int typeCode = (int)bytes.bytesInRange(4,11);

        return typeCode(typeCode);
    }

}
