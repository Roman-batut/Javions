package ch.epfl.javions;

public class WebMercator {
    private WebMercator(){

    }
    public static double x(int zoomLevel, double longitude){
        double x = Math.scalb(Units.convertTo(longitude, Units.Angle.TURN)+(1/2),8+zoomLevel);
        return x;
    }
    public static double y(int zoomLevel, double latitude){
        double y = Math.scalb(Units.convertTo(-Math2.asinh(Math.tan(latitude)),Units.Angle.TURN)+(1/2),8+zoomLevel);
        return y;
    }
}
/*
 *	Author:      Br4v0r
 *	Date:        22/02/2023
 */
