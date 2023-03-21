package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;


public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress,int category,CallSign callSign)
        implements Message{

    public AircraftIdentificationMessage{
        Preconditions.checkArgument(timeStampNs>=0);
        if(icaoAddress == null || callSign ==null){
            throw new NullPointerException();
        }
    }
    @Override
    public long timeStampNs() {
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        IcaoAddress icaoAddres = rawMessage.icaoAddress();
        long timestampNs = rawMessage.timeStampNs();
//        int category = rawMessage;
        return null  ;
    }
}
