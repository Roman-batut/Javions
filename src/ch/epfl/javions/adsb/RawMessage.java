package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.aircraft.IcaoAddress;

public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;
    private static final byte DF = 0b00_01_00_01;

    public RawMessage{
        if(timeStampNs<0 || bytes.size()!=LENGTH){
            throw new IllegalArgumentException();
        }
    }

    public static RawMessage of(long timeStampNs, byte[] bytes){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        if(crc24.crc(bytes) != 0) {
            return null;
        }
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    public static int size(byte byte0){
        int b = Bits.extractUInt(byte0,0,6);
        if(b == DF){
            return LENGTH;
        }
        return 0;
    }

    public static int typeCode(long payload){

        return 0;
    }

    public int downLinkFormat(){

        return 0;
    }

    public IcaoAddress icaoAddress(){

        return null;
    }

    public long payload(){

        return 0;
    }

    public int typeCode(){

        return 0;
    }

}
