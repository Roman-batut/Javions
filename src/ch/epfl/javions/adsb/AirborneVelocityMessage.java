package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Vector;

public record AirborneVelocityMessage(long timeStampNs,IcaoAddress icaoAddress,double speed,double trackOrHeading) implements Message{


    public AirborneVelocityMessage{
        Preconditions.checkNotNull(icaoAddress);

        Preconditions.checkArgument(speed>=0 || trackOrHeading>=0 || timeStampNs>=0);
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int st = Bits.extractUInt(payload,48,3);
        long infos = Bits.extractUInt(payload, 21,22);

        if(st == 1 || st == 2){
            int Vns = Bits.extractUInt(infos, 0,10)-1;
            int Dns = Bits.extractUInt(infos, 10,1);
            int Vew = Bits.extractUInt(infos, 11,10)-1;
            int Dew = Bits.extractUInt(infos, 21,1);
            if(Vns < 0 || Vew < 0){
                return null;
            }

            int Vy = speedcalculator(st, Vns);
            int Vx = speedcalculator(st, Vew);
            double speed = Units.convertFrom(Math.hypot(Vy, Vx), Units.Speed.KNOT);

            if(Dns == 1) {Vy = -Vy;}
            if(Dew == 1) {Vx = -Vx;}

            double angle = Math.atan2(Vx, Vy);
            if(angle < 0){
                angle += Units.Angle.TURN;
            }

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);

        } else if(st == 3 || st == 4){
            int as = Bits.extractUInt(infos, 0,10)-1;
            int Hdg = Bits.extractUInt(infos, 11,10);
            int sh = Bits.extractUInt(infos, 21,1);
            if(as < 0 || sh == 0){
                return null;
            }

            double speed = Units.convertFrom(speedcalculator(st-2, as), Units.Speed.KNOT);
            double angle = Units.convert(Hdg/Math.scalb(1d, 10), Units.Angle.TURN, Units.Angle.RADIAN);

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);
        } else {
             return null;
        }
    }


    private static int speedcalculator (int subtype, int speed){
        return (int) (speed * Math.pow(subtype, 2));
    }
}
