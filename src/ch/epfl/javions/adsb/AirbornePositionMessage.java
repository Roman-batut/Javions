package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs,IcaoAddress icaoAddress,double altitude,int parity,double x,double y)
        implements Message{

    private static final double NORMALISATION = Math.scalb(1d, 27);

    public AirbornePositionMessage {
        if(icaoAddress == null){
            throw new NullPointerException();
        }
        Preconditions.checkArgument(timeStampNs>=0);
        Preconditions.checkArgument(parity==1 || parity==0);
        Preconditions.checkArgument(0<=x || x<1);
        Preconditions.checkArgument(0<=y || y<1);
    }
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    public static AirbornePositionMessage of(RawMessage rawMessage){
        int typecode = rawMessage.typeCode();
        long payload = rawMessage.payload();

        if(typecode>=9 && typecode<=18 ||typecode>=20 && typecode<=22){
            return null;
        }

        int format = Bits.extractUInt(payload, 34,1);
        double latitude = (Bits.extractUInt(payload, 17,17))/NORMALISATION;
        double longitude = (Bits.extractUInt(payload, 0,17))/NORMALISATION;
        int alt  = Bits.extractUInt(payload, 41, 12);

        if (Bits.extractUInt(alt, 4, 1) == 1){
           double height = -1000 + ((alt & 0b1111_1110_0000)>>>1 & (alt & 0b0000_0000_1111))*25;
           height = Units.convertFrom(height, Units.Length.FOOT);
           return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), height, format,
                   latitude, longitude);
        }
        else {
            int[] val = new int[]{4, 2, 0, 10, 8, 6, 5, 3, 1, 11, 9, 7};
            int untangled = 0b0000_0000_0000;
            for (int i=0 ; i<val.length ; i++) {
                untangled = untangled | ((Bits.extractUInt(alt, val[i], 1)) << (val.length-i-1));
            }

            int strongbyte = greydecode(Bits.extractUInt(untangled,4,9), 9);
            int weakbyte = greydecode(Bits.extractUInt(untangled,1,3),3);

            if(weakbyte == 0|| weakbyte==5||weakbyte==6){
                return null;
            }
            if (weakbyte == 7){
                weakbyte = 5;
            }
            if(strongbyte%2 == 0){
                weakbyte = 6-weakbyte;
            }
            double height = -1300+(weakbyte*1000)+(strongbyte*500);
            height = Units.convertFrom(height, Units.Length.FOOT);
            return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), height, format,
                    latitude, longitude);
        }
    }

    private static int greydecode(int greycode, int lenght){
        int decoded = 0;
        for (int i=0 ; i<lenght ; i++) {
            decoded = decoded ^ (greycode >>i);
        }
        return decoded;
    }

//  #TODO FAIRE DE PARTOUT DES RETOUVHES POUR PAS AVOIR DE VARIABLES REDONDANTES ETC CA FAIT PERDRE DES POINTS APPAREMENT

}
