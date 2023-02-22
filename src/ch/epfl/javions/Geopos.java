package ch.epfl.javions;


import java.nio.FloatBuffer;

public record Geopos(int longitudeT32, int latitudeT32) {
    public Geopos{
        if(!isValidLatitudeT32(latitudeT32)){
            throw new IllegalArgumentException();
        }
    }

    public static boolean isValidLatitudeT32(int latitudeT32) {
        if (latitudeT32 == Math2.clamp((int) Math.scalb(1, -30), latitudeT32, (int) Math.scalb(1, 30))) {
            return true;
        } else {
            return false;
        }
    }
    public double longitude(){
        double longrad = Units.convertFrom(longitudeT32, Units.Angle.T32);
        return longrad;
    }
    public double latitude(){
        double latrad = Units.convertFrom(latitudeT32, Units.Angle.T32);
        return latrad;
    }

    @Override
    public String toString() {
        double latdeg = Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
        double longdeg = Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
        return (latdeg +""+ longdeg);
    }
}
