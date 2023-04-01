package ch.epfl.javions;

/**
 * Class containing useful math functions.
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class Math2 {

    //* Constructor

    /**
     * Private constructor to prevent instantiation
     */
    private Math2(){}


    //* Methods

    /**
     * Returns the value of the given integer clamped between the given bounds.
     * @param min the lower bound
     * @param v the value to be clamped
     * @param max the upper bound
     * @return the clamped value
     * @throws IllegalArgumentException if the bounds are not valid
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(!(min > max));

        if (v > max){
            return max;
        }
        else return Math.max(v, min);
    }

    /**
     * @param x a double
     * @return arcsinh(x)
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+(x*x)));
    }
}
//#TODO ICI A VOIR POUR ASINH