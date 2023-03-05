package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 *  Record representing an aircraft registration
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public record AircraftRegistration(String string) {

    //* Constructor

    /**
     *  Constructor of an aircraft registration
     *  @param string the string representing the aircraft registration
     *  @throws IllegalArgumentException if the string is not a valid aircraft registration or is empty
     */
    public AircraftRegistration{
        Pattern pattern = Pattern.compile("[A-Z0-9 .?/_+-]+");
        if(!pattern.matcher(string).matches() || string.isEmpty()){
            throw new IllegalArgumentException();
        }
    }
}
