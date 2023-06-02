package ch.epfl.javions.aircraft;

/**
 *  Enum representing the wake turbulence category of an aircraft
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public enum WakeTurbulenceCategory {

    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    //* Methods

    /**
     *  Wake turbulence category of a given string
     *  @param s the string representing the wake turbulence category
     *  @return the string representing the wake turbulence category,
     *  or UNKNOWN if the string is not a valid wake turbulence category
     */
    public static WakeTurbulenceCategory of(String s){
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}

