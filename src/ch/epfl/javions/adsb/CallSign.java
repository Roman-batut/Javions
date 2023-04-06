package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 *  Record representing a call sign
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public record CallSign(String string) {

    //* Constructor

    /**
     *  Constructor of a call sign
     *  @param string the string representing the call sign
     *  @throws IllegalArgumentException if the string is not a valid call sign
     */
    public CallSign{
        if(!string.isEmpty()) {
            Pattern pattern = Pattern.compile("[A-Z0-9 ]{0,8}");

            Preconditions.checkArgument(pattern.matcher(string).matches());
        }
    }
}
