package ch.epfl.javions;

/**
 * Class containing units conversions functions.
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class Units {

    //* Constants

    // One hundredth of a unit
    public static final double CENTI = 1e-2;
    // One thousand of a unit
    public static final double KILO = 1e3;

    /**
     * Important constants of angles, lengths, times and speeds in different units expressed in SI units
     */
    public final static class Angle{
        // Private constructor to prevent instantiation
        private Angle(){}
        // Angle of the arc of a circle whose length is equal to its radius
        public static final double RADIAN = 1;
        // Angle of a whole circle
        public static final double TURN = 2*Math.PI*RADIAN;
        // One three hundred and sixtieth of a whole circle
        public static final double DEGREE = TURN/360;
        // One two to the power of thirty-second of a whole circle
        public static final double T32 = Math.scalb(TURN, -32);
    }

    public final static class Length{
        // Private constructor to prevent instantiation
        private Length(){}
        // One meter
        public static final double METER = 1;
        // One hundredth of a meter
        public static final double CENTIMETER = CENTI*METER;
        // One thousand meters
        public static final double KILOMETER = KILO*METER;
        // One inch
        public static final double INCH = 2.54*CENTIMETER;
        // One foot, twelve inches
        public static final double FOOT = 12*INCH;
        // One thousand eight hundred and fifty-two meters
        public static final double NAUTICAL_MILE = 1852*METER;
    }

    public final static class Time{
        // Private constructor to prevent instantiation
        private Time(){}
        // One second
        public static final double SECOND = 1;
        // Sixty seconds
        public static final double MINUTE = 60*SECOND;
        // Three thousand six hundred seconds, sixty minutes
        public static final double HOUR = 60*MINUTE;
    }

    public final static class Speed{
        // Private constructor to prevent instantiation
        private Speed(){}
        // One nautical mile per hour
        public static final double KNOT = Length.NAUTICAL_MILE/Time.HOUR;
        // One thousand meter per second
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER/Time.HOUR ;
    }


    //* Constructor

    /**
     * Private constructor to prevent instantiation
     */
    private Units (){}


    //* Methods

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
