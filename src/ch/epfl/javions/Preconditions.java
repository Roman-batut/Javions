package ch.epfl.javions;

/**
 * Class to check preconditions
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class Preconditions {

    //* Constructor

    /**
     * Private constructor to prevent instantiation
     */
    private Preconditions() {}


    //* Methods
    
    
    /**
     * Checks that the given value is not false
     * @param shouldBeTrue the value to be checked 
     * @throws IllegalArgumentException if the given value is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks that one of the given value is not null
     * @param shouldBeNotNull the value to be checked 
     * @throws NullPointerException if the given value is null
     */
    public static void checkNotNull(Object ... shouldBeNotNull){
        for (Object o : shouldBeNotNull){
            if (o == null){
                throw new NullPointerException();
            }
        }
    }
}
//#TODO ICI liste d'objet ?
