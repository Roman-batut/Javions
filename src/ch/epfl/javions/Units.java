package ch.epfl.javions;

/**
 * Class containing units conversions functions.
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class Units {
    
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    //* Constructor

    /**
     * Private constructor to prevent instantiation
     */
    private Units (){}


    //* Methods


    /**
     * Important constants of angles, lengths, times and speeds in different units expressed in SI units
     */
    public final static class Angle{
        /**
         * Angles units
         */
        private Angle(){}
        public static final double RADIAN = 1;
        public static final double TURN = 2*Math.PI*RADIAN;
        public static final double DEGREE = TURN/360;
        public static final double T32 = Math.scalb(TURN, -32);
    }
    public final static class Length{
        /**
         * Length units
         */
        private Length(){}
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI*METER;
        public static final double KILOMETER = KILO*METER;
        public static final double INCH = 2.54*CENTIMETER;
        public static final double FOOT = 12*INCH;
        public static final double NAUTICAL_MILE = 1852*METER;
    }
    public final static class Time{
        /**
         * Time units
         */
        private Time(){}
        public static final double SECOND = 1;
        public static final double MINUTE = 60*SECOND;
        public static final double HOUR = 60*MINUTE;
    }
    public final static class Speed{
        /**
         * Speed units
         */
        private Speed(){}
        public static final double KNOT = Length.NAUTICAL_MILE/Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER/Time.HOUR ;
    }

    /**
     * Converts a value from one unit to another from previously defined constants
     * @param value the value to be converted
     * @param fromUnit the unit of the value
     * @param toUnit the unit to convert to
     * @return the converted value
     */
    public static double convert(double value, double fromUnit, double toUnit){
        return value*(fromUnit/toUnit);
    }

    /**
     * Same as convert but considers toUnit = 1
     */
    public static double convertFrom(double value, double fromUnit){
        return convert(value, fromUnit, 1);
    }

    /**
     * Same as convert but considers fromUnit = 1
     */
    public static double convertTo(double value, double toUnit){
        return convert(value, 1, toUnit);
    }
}

// #TODO regarder pour plus de docu ici