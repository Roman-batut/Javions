package ch.epfl.javions;

/**
 * A record class that represents a geographical position
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {

    //* Constructor

    /**
     * Public constructor that checks the validity of the given values
     * @throws IllegalArgumentException if the given values are not valid
     */
    public GeoPos {
        if(!isValidLatitudeT32(latitudeT32)){
            throw new IllegalArgumentException();
        }
    }


    //* Methods


    /**
     * Returns true if the given value is a valid latitude in T32 format
     * @param latitudeT32 the latitude to be tested
     * @return true if the given value is a valid latitude in T32 format
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        return Math.scalb(-1d,30)<=latitudeT32 &&  Math.scalb(1d,30)>=latitudeT32;
    }

    /**
     * @return a converted longitude in radians
     */
    public double longitude(){
        double longrad = Units.convertFrom(longitudeT32, Units.Angle.T32);
        return longrad;
    }

    /**
     * @return a converted latitude in radians
     */
    public double latitude(){
        double latrad = Units.convertFrom(latitudeT32, Units.Angle.T32);
        return latrad;
    }


    //* Object overrides


    /**
     * @return a string representation of the object in degrees
     */ 
    @Override
    public String toString(){
        double latdeg = Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
        double longdeg = Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
        return ("("+latdeg+"°, "+ longdeg+"°)");
    }
}
