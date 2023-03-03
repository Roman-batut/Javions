package ch.epfl.javions.aircraft;

/**
 *  Enum representing the wake turbulence category of an aircraft
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public enum WakeTurbulenceCategory {

    LIGHT("L"),
    MEDIUM("M"),
    HEAVY("H"),
    UNKNOWN("");

    private final String letter;

    //* Constructor

    /**
     *  Constructor of a wake turbulence category
     *  @param s the string representing the wake turbulence category
     */
    private WakeTurbulenceCategory(String s) {
        letter = s;
    }


    //* Methods
    
    
    /**
     *  Returns the string representing the wake turbulence category,
     *  or UNKNOWN if the string is not a valid wake turbulence category
     *  @param s the string representing the wake turbulence category
     */
    public static WakeTurbulenceCategory of(String s){
        for(WakeTurbulenceCategory category : WakeTurbulenceCategory.values()) {
            if (category.letter.equals(s)) {
                return category;
            }
        }

        return UNKNOWN;
    }
}

