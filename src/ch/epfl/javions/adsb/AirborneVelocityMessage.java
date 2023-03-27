package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs,IcaoAddress icaoAddress,double speed,double trackOrHeading) implements Message{


    public AirborneVelocityMessage{
        //if(icaoAddress == null){
        //    throw new  NullPointerException();
        //}
        Preconditions.checkNotNull(icaoAddress);

        Preconditions.checkArgument(speed>=0 || trackOrHeading>=0 || timeStampNs>=0);
    }

    @Override
    public long timeStampNs() {
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int st = Bits.extractUInt(payload,48,3);
        long infos = Bits.extractUInt(payload, 21,22);
        if(st ==1 || st ==2){
            int Vns = Bits.extractUInt(infos, 0,10);
            int Dns = Bits.extractUInt(infos, 10,1);
            int Vew = Bits.extractUInt(infos, 11,10);
            int Dew = Bits.extractUInt(infos, 21,1);
        }else if(st == 3 || st == 4){
            int as = Bits.extractUInt(infos, 0,10);
            int Hdg = Bits.extractUInt(infos, 11,10);
            int sh = Bits.extractUInt(infos, 21,1);
        }

        return null;
    }
}
