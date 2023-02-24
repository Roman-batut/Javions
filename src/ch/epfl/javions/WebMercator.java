package ch.epfl.javions;

/**
 * Class useful for Web Mercator projection
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public class WebMercator {

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
        int power = 8+zoomLevel;
        double x = Math.scalb((Units.convertTo(longitude, Units.Angle.TURN)+(0.5)),power);
        return x;

    }

    /**
     * @param zoomLevel the zoom level
     * @param latitude the latitude
     * @return the y coordinate
     */
    public static double y(int zoomLevel, double latitude){
        int power = 8+zoomLevel;
        double y = Math.scalb(Units.convertTo(-Math2.asinh(Math.tan(latitude)),Units.Angle.TURN)+(0.5),power);

        return y;
    }
}

