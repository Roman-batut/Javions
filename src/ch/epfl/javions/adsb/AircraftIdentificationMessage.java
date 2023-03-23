package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;


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
        long payload = rawMessage.payload();
        IcaoAddress icaoAddres = rawMessage.icaoAddress();
        long timestampNs = rawMessage.timeStampNs();
        byte strcategory = (byte)((14-rawMessage.typeCode())<<4);
        byte weakcategory = (byte)(Bits.extractUInt(payload, 53,3));
        int category  = strcategory |weakcategory;
        CallSign callSign1 = callSignextraction(payload);
        return new AircraftIdentificationMessage(timestampNs, icaoAddres, category,callSign1);
    }

    private static CallSign callSignextraction(long payload){
//        StringBuilder b = new StringBuilder();
//        for (int i = 47; i >= 0; i-=6) {
//            b.append(HexFormat.of().withUpperCase().toHexDigits(Bits.extractUInt(payload, i , 6), 6));
//        }
//        return new CallSign(b.toString());
//        #TODO pas bon ca faut faire cas par cas je pense cf enoncé
        return null;
    }
}
