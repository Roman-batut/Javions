package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 *  Record representing an ICAO address
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public record IcaoAddress(String string) {

    //* Constructor

    /**
     *  Constructor of an ICAO address
     *  @param string the string representing the ICAO address
     *  @throws IllegalArgumentException if the string is not a valid ICAO address or is empty
     */
    public IcaoAddress{
        Pattern pattern = Pattern.compile("[0-9A-F]{6}");
        Preconditions.checkArgument(!(!pattern.matcher(string).matches() || string.isEmpty()));
    }
}
