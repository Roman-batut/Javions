package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;
    private static final byte DF = 0b00_01_00_01;

    public RawMessage{
        Preconditions.checkArgument(timeStampNs<0);
        Preconditions.checkArgument(bytes.size()!=LENGTH);
    }

    public static RawMessage of(long timeStampNs, byte[] bytes){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        if(crc24.crc(bytes) != 0) {
            return null;
        }
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    public static int size(byte byte0){
        int b = Bits.extractUInt(byte0,3,5);
        if(b == DF){
            return LENGTH;
        }
        return 0;
    }

    public static int typeCode(long payload){
        int typecode = (int)payload >>>51;
        return typecode;
    }

    public int downLinkFormat(){

        return (int)bytes.bytesInRange(0,5);
    }

    public IcaoAddress icaoAddress(){
        long icao = bytes.bytesInRange(8,24);
        HexFormat b = HexFormat.of();
        b.toHexDigits(icao, 6);
        return new IcaoAddress(b.toString());
    }

    public long payload(){
        long mE = bytes.bytesInRange(29,51);
        return mE;
    }

    public int typeCode(){
        int typeCode = (int)bytes.bytesInRange(24,5);
        return typeCode;
    }

}
