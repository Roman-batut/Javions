package ch.epfl.javions;

public class WebMercator {
    private WebMercator(){
    }

    public static double x(int zoomLevel, double longitude){
        int power = 8+zoomLevel;
//        double x = Math.scalb((Units.convertTo(longitude, Units.Angle.TURN)+(1/2)),power);
        double x = Math.pow(2,power)*(Units.convertTo(longitude, Units.Angle.TURN)+(1/2));
        return x;

    }

    public static double y(int zoomLevel, double latitude){
        int power = 8+zoomLevel;
        double y = Math.scalb(Units.convertTo(-Math2.asinh(Math.tan(latitude)),Units.Angle.TURN)+(1/2),power)+256;

        return y;
    }
}

