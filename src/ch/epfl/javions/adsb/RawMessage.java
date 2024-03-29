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
public record RawMessage(long timeStampNs,
                         ByteString bytes) {

    //* Constants

    // Length of a raw message in bytes
    public static final int LENGTH = 14;
    
    private static final byte DF = 0b00_01_00_01;

    private static final int DF_START = 3;
    private static final int DF_SIZE = 5;
    private static final int DF_BYTE = 0;

    private static final int ICAO_BYTE_START = 1;
    private static final int ICAO_BYTE_SIZE = 4;
    private static final int ICAO_STRING_SIZE= 6;

    private static final int ME_BYTE_START = 4;
    private static final int ME_BYTE_SIZE = 11;
    private static final int ME_LENGTH = 56;
    private static final int TYPECODE_LENGHT = 5;
    private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();

    //* Constructor

    /**
     *  Constructor of a raw message
     *  @param timeStampNs the time stamp of the message in nanoseconds
     *  @param bytes the bytes of the message
     *  @throws IllegalArgumentException if the time stamp is negative or if the bytes are not of length 14
     */
    public RawMessage{
        Preconditions.checkArgument(!(timeStampNs<0));
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }


    //* Methods

    /**
     *  Creates a raw message from a time stamp and a byte array
     *  @param timeStampNs the time stamp of the message in nanoseconds
     *  @param bytes the bytes of the message
     *  @return a raw message or null if the CRC24 of the bytes is not 0
     */
    public static RawMessage of(long timeStampNs, byte[] bytes){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);

        return crc24.crc(bytes) != 0 ? null : new RawMessage(timeStampNs, new ByteString(bytes));
    }

    /**
     *  Size of a message in bytes
     *  @param byte0 the first byte of the message
     *  @return the size of the message in bytes,
     *  the constant LENGTH if the DF is 17, 0 otherwise
     */
    public static int size(byte byte0){
        int bit = Bits.extractUInt(byte0, DF_START, DF_SIZE);

        return ((bit == DF) ? LENGTH : 0);
    }

    /**
     *  Type code of a message
     *  @param payload the payload of the message
     *  @return the type code of the message,
     *  the type code of its ME field
     */
    public static int typeCode(long payload){
        return Bits.extractUInt(payload, (ME_LENGTH - TYPECODE_LENGHT) ,TYPECODE_LENGHT);

    }

    /**
     *  Down link format of a message
     *  @return the down link format of the message,
     *  its DF field (the five most significant bits of the first byte)
     */
    public int downLinkFormat(){
        int byte0 = (int)bytes.bytesInRange(DF_BYTE, 1);

        return Bits.extractUInt(byte0, DF_START, DF_SIZE);
    }

    /**
     *  ICAO address of the aircraft
     *  @return the ICAO address of the aircraft
     */
    public IcaoAddress icaoAddress(){
        long icao = bytes.bytesInRange(ICAO_BYTE_START, ICAO_BYTE_SIZE);
        String icaoString = HEX_FORMAT.toHexDigits(icao, ICAO_STRING_SIZE);
        
        return new IcaoAddress(icaoString);
    }
    
    /**
     *  Payload of the message
     *  @return the payload of the message,
     *  it's ME field
     */
    public long payload(){
        return bytes.bytesInRange(ME_BYTE_START, ME_BYTE_SIZE);
    }

    /**
     *  Type code of the message
     *  @return the type code of the message,
     *  the five most significant bits of its ME field
     */
    public int typeCode(){
        return typeCode(payload());
    }
}
