package ch.epfl.javions;

/**
 * Class useful for Web Mercator projection
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public class WebMercator {

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
        int power = 8 + zoomLevel;
        double x = Units.convertTo(longitude, Units.Angle.TURN) + (ADD_CST);

        return Math.scalb(x, power);
    }

    /**
     * @param zoomLevel the zoom level
     * @param latitude the latitude
     * @return the y coordinate
     */
    public static double y(int zoomLevel, double latitude){
        int power = 8 + zoomLevel;
        double angle = -Math2.asinh(Math.tan(latitude));
        double y = Math.scalb((Units.convertTo( angle, Units.Angle.TURN) + (ADD_CST)), power);

        return y;
    }
}

