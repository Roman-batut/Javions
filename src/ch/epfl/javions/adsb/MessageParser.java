package ch.epfl.javions.adsb;

/**
 * Class representing a parser of ADS-B messages
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public class MessageParser {

    //* Constants

    private static final int VEL_TYPECODE = 19;
    private static final int[] POS_STARTS_TYCODE = new int[]{9,20};
    private static final int[] POS_ENDS_TYCODE = new int[]{18,22};
    private static final int[] ID_TYCODES = new int[]{1,2,3,4};

    //* Constructor

    private MessageParser(){
    }


    //* Methods

    /**
     * Parses a raw message and returns the corresponding ADS-B message
     * @param rawMessage the raw message
     * @throws NullPointerException if the raw message is null
     * @return the ADS-B message corresponding to the raw message,
     * or null if one of the raw message's fields is invalid
     */
    public static Message parse(RawMessage rawMessage){
        int typeCode = rawMessage.typeCode();

        if(typeCode == VEL_TYPECODE){
            return AirborneVelocityMessage.of(rawMessage);
        } else if((typeCode >= POS_STARTS_TYCODE[0] && typeCode <= POS_ENDS_TYCODE[0])
                || (typeCode >= POS_STARTS_TYCODE[1] && typeCode <= POS_ENDS_TYCODE[1])){

            return AirbornePositionMessage.of(rawMessage);
        }else if (typeCode == ID_TYCODES[0] || typeCode == ID_TYCODES[1]
                || typeCode == ID_TYCODES[2] ||typeCode == ID_TYCODES[3]) {

            return AircraftIdentificationMessage.of(rawMessage);
        } else {
            return null;
        }
    }
}
