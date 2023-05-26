package ch.epfl.javions;

/**
 * Class useful for Web Mercator projection
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class WebMercator {

    //* Constants

    private final static double ADD_CST = 0.5;

    //* Constructor

    /**
     * Private constructor to prevent instantiation
     */
    private WebMercator(){
    }


    //* Methods

    /**
     * @param zoomLevel the zoom level
     * @param longitude the longitude 
     * @return the x coordinate
     */
    public static double x(int zoomLevel, double longitude){
        return coordinate(zoomLevel, longitude);
    }

    /**
     * @param zoomLevel the zoom level
     * @param latitude the latitude
     * @return the y coordinate
     */
    public static double y(int zoomLevel, double latitude){
        return coordinate(zoomLevel, -Math2.asinh(Math.tan(latitude)));
    }

    //* Private methods

    private static double coordinate(int zoomLevel, double angle){
        int power = 8 + zoomLevel;
        double coordinate = Units.convertTo(angle, Units.Angle.TURN) + (ADD_CST);

        return Math.scalb(coordinate, power);
    }
}
