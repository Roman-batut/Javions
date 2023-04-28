package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 *  Record representing an aircraft type designator
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public record AircraftTypeDesignator(String string) {

    //* Constructor

    /**
     *  Constructor of an aircraft type designator
     *  @param string the string representing the aircraft type designator
     *  @throws IllegalArgumentException if the string is not a valid aircraft type designator
     */
    public AircraftTypeDesignator {
        Pattern pattern = Pattern.compile("[A-Z0-9]{2,4}");

        Preconditions.checkArgument(pattern.matcher(string).matches());
    }
}
// #TODO en vrai voir (Icaoaddress...) si tout fonctionne bien genre plus de is empty parceque pattern et enlever les negations
