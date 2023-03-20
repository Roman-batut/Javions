package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

public class CprDecoder {

    private CprDecoder(){

    }

    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 1 || mostRecent == 0);
        return null;
    }
}
/*
 *	Author:      Br4v0r
 *	Date:
 */
