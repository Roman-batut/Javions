package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 *  Record representing an aircraft description
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public record AircraftDescription(String string) {

    //* Constructor

    /**
     *  Constructor of an aircraft description
     *  @param string the string representing the aircraft description
     *  @throws IllegalArgumentException if the string is not a valid aircraft description
     */
    public AircraftDescription {
        if(!string.isEmpty()) {
            Pattern pattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
            Preconditions.checkArgument(pattern.matcher(string).matches());
        }
    }
}
