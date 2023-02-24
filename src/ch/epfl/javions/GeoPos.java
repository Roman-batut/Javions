package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32) {
    public GeoPos {
        if(!isValidLatitudeT32(latitudeT32)){
            throw new IllegalArgumentException();
        }
    }

    public static boolean isValidLatitudeT32(int latitudeT32) {
        return -Math.pow(2,30)<=latitudeT32 && Math.pow(2,30)>=latitudeT32;
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
        return ("("+latdeg+"°, "+ longdeg+"°)");
    }
}
