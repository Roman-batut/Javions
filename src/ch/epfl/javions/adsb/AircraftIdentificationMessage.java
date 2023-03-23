package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import javax.swing.text.AttributeSet;
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
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        if(validmessage(payload)) {
            IcaoAddress icaoAddres = rawMessage.icaoAddress();
            long timestampNs = rawMessage.timeStampNs();
            byte strcategory = (byte) ((14 - rawMessage.typeCode()) << 4);
            byte weakcategory = (byte) (Bits.extractUInt(payload, 53, 3));
            int category = strcategory | weakcategory;
            CallSign callSign1 = callSignextraction(payload);
            return new AircraftIdentificationMessage(timestampNs, icaoAddres, category, callSign1);
        }

        return null;
    }

    private static CallSign callSignextraction(long payload){
        char[] chartab = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        int[] charactervalue = new int[8];
        int k = 0;
        for (int i=47 ; i>=0 ; i-=6) {
            charactervalue[k] = Bits.extractUInt(payload,i,6);
            k++;
        }

        String callsignstring = "";
        int missing = 0;
        for (int i=0 ; i<8 ; i++) {
            if((charactervalue[i]>=1 && charactervalue[i]<=26)||(charactervalue[i]>=48 && charactervalue[i]<=57)|| charactervalue[i] == 32){
                if(charactervalue[i]<27){
                    callsignstring += chartab[charactervalue[i]];
                }
                else{
                    callsignstring += Character.toChars(charactervalue[i]);
                }
            }
            else {
                missing++;
            }
        }
        for (int i=callsignstring.length()-missing ; i<8 ; i++){
            callsignstring += " ";
        }

        return new CallSign(callsignstring);
    }

    private static boolean validmessage(long payload) {
        return (RawMessage.typeCode(payload) == 1 ||RawMessage.typeCode(payload) == 2
                ||RawMessage.typeCode(payload) == 3 ||RawMessage.typeCode(payload) == 4);
    }
}
