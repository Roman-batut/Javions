package ch.epfl.javions.adsb;

/**
 * Class representing a parser of ADS-B messages
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public class MessageParser {

    //* Constructor

    private MessageParser(){
    }


    //* Methods
    
    
    /**
     * Returns the ADS-B message corresponding to the raw message,
     * or null if one of the raw message's fields is invalid
     * @param rawMessage the raw message
     * @throws NullPointerException if the raw message is null
     */
    public static Message parse(RawMessage rawMessage){

        int typeCode = rawMessage.typeCode();

        if(typeCode == 19){
            return AirborneVelocityMessage.of(rawMessage);
        } else if((typeCode>=9 && typeCode<=18) || (typeCode>=20 && typeCode<=22)){
            return AirbornePositionMessage.of(rawMessage);
        }else if (typeCode == 1 || typeCode == 2 || typeCode == 3 ||typeCode == 4) {
            return AircraftIdentificationMessage.of(rawMessage);
        } else {
            return null;
        }
    }
}
