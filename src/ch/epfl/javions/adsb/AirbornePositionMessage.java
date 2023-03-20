package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs,IcaoAddress icaoAddress,double altitude,int parity,double x,double y)
        implements Message{

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
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

    public static AirbornePositionMessage of(RawMessage rawMessage){
        return null;
    }
//  #TODO FAIRE DE PARTOUT DES RETOUVHES POUR PAS AVOIR DE VARIABLES REDONDANTES ETC CA FAIT PERDRE DES POINTS APPAREMENT

}
