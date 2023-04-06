package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Record representing an ADS-B aircraft identification message
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign)
        implements Message{

    private static final int WEAK_START = 48;
    private static final int WEAK_SIZE = 3;
    private static final int STRONG_BIT_REGUL = 14;

    private static final int CALLSIGN_MAX_SIZE = 8;

    private static final int CHAR1_BIT_POS = 42;
    private static final int CHAR_SIZE = 6;

    private static final int ALPHABETICAL_VALUE_START = 1;
    private static final int ALPHABETICAL_VALUE_END = 26;
    private static final int NUMERICAL_VALUE_START = 48;
    private static final int NUMERICAL_VALUE_END = 57;
    private static final int SPACE_VALUE = 32;

    //* Constructor

    /**
     * Constructor of an aircraft identification message
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param icaoAddress the ICAO address of the aircraft
     * @param category the category of the aircraft
     * @param callSign the call sign of the aircraft
     * @throws NullPointerException if the ICAO address or the call sign is null
     * @throws IllegalArgumentException if the time stamp is negative
     */
    public AircraftIdentificationMessage{
        Preconditions.checkArgument(timeStampNs>=0);
        
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
    }

    //* Getters

    /**
     * Returns the time stamp of the message in nanoseconds
     */
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    /**
     * Returns the ICAO address of the aircraft
     */
    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }


    //* Methods


    /**
     * Returns the aircracft identification message corresponding to the raw message,
     * or null if one of the raw message's fields is invalid
     * @param rawMessage the raw message
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();

        int strCategory = ((STRONG_BIT_REGUL - rawMessage.typeCode()) << (Byte.SIZE/2));
        int weakCategory = (Bits.extractUInt(payload, WEAK_START, WEAK_SIZE));

        long timestampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddres = rawMessage.icaoAddress();
        int category = strCategory | weakCategory;
        CallSign callSign1 = callSignextraction(payload);
        if(callSign1 == null){
            return null;
        }

        return new AircraftIdentificationMessage(timestampNs, icaoAddres, category, callSign1);
    }

    //* Private methods

    private static CallSign callSignextraction(long payload){
        char[] chartab = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        int[] charactervalue = new int[CALLSIGN_MAX_SIZE];
        int k = 0;
        for (int i=CHAR1_BIT_POS ; i>=0 ; i-=CHAR_SIZE) {
            charactervalue[k] = Bits.extractUInt(payload,i,CHAR_SIZE);
            k++;
        }

        StringBuilder callsignstring = new StringBuilder();
        for (int i=0 ; i<CALLSIGN_MAX_SIZE ; i++) {
            if ((charactervalue[i] >= ALPHABETICAL_VALUE_START && charactervalue[i] <= ALPHABETICAL_VALUE_END) ||
                    (charactervalue[i] >= NUMERICAL_VALUE_START && charactervalue[i] <= NUMERICAL_VALUE_END) ||
                    charactervalue[i] == SPACE_VALUE) {

                if (charactervalue[i] <= ALPHABETICAL_VALUE_END) {
                    callsignstring.append(chartab[charactervalue[i] - 1]);
                } else {
                    callsignstring.append(Character.toChars(charactervalue[i]));
                }

            }
            else { return null; }
        }

        return new CallSign(callsignstring.toString().stripTrailing());
    }
}
