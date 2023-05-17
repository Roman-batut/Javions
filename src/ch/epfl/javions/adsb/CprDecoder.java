package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * Class representing a CPR decoder
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class CprDecoder {

    private final static double[] Z_PHI = new double[]{60, 59};
    private final static double[] DELTA_PHI = new double[]{(1d/Z_PHI[0]), (1d/Z_PHI[1])};
    private static final double TWO_PI = 2 * Math.PI;

    //* Constructor

    private CprDecoder(){
    }


    //* Methods

    /**
     * Decodes the aircraft position
     * @param x0 the longitude coordinate of the first message
     * @param y0 the latitude coordinate of the first message
     * @param x1 the longitude coordinate of the second message
     * @param y1 the latitude coordinate of the second message
     * @param mostRecent the most recent message (0 or 1)
     * @throws IllegalArgumentException if the most recent message is not 0 or 1
     * @return the geographical position of an aircraft.
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 1 || mostRecent == 0);

        //latitude calculation (phi)
        double[] phi = coordinateCalculation(y0, y1, Z_PHI, DELTA_PHI);

        //longitude calculation (lambda)
        double A = aCalculation(phi, 0);
        double ATest = aCalculation(phi, 1);

        double[] ZLambda = Double.isNaN(A) ? new double[]{1, 0} : lambdaCalculation(A);

        double[] lambda = Double.isNaN(A) ? new double[]{x0, x1} : coordinateCalculation(x0, x1, lambdaCalculation(A), new double[]{(1d / ZLambda[0]), (1d / ZLambda[1])});

        if(!Double.isNaN(A) && !isValidLambda(ATest, ZLambda[0])) {
            return null;
        }

        //exception handler
        if(!(isValidCoordinate(lambda[mostRecent]) && isValidCoordinate(phi[mostRecent]))){
            return null;
        }

        //conversion
        return new GeoPos(convertedCoordinate(lambda[mostRecent]), convertedCoordinate(phi[mostRecent]));
    }

    //* Private methods

    private static double[] coordinateCalculation(double coord0, double coord1, double[] specificZ, double[] specificDelta){
        double z = Math.rint(specificZ[1]*coord0 - specificZ[0]*coord1);
        double[] zTab = new double[]{z, z};
        double[] out = new double[]{coord0, coord1};

        for (int i=0 ; i<2 ; i++) {
            if(z < 0){
                zTab[i] += specificZ[i];
            }
            out[i] = specificDelta[i] * (zTab[i] + out[i]);
        }

        return out;
    }

    private static double aCalculation(double[] phi, int index){
        double ANum = (1 - Math.cos(TWO_PI * DELTA_PHI[0]));

        double ADen = (Math.cos(Units.convert(phi[index], Units.Angle.TURN, Units.Angle.RADIAN)) * Math.cos(Units.convert(phi[index], Units.Angle.TURN, Units.Angle.RADIAN)));
        double A = Math.acos(1 - (ANum / ADen));

        return A;
    }

    private static double[] lambdaCalculation(double A){
        double[] ZLambda = new double[2];

        ZLambda[0] = Math.floor(TWO_PI / A);
        ZLambda[1] = ZLambda[0] - 1;

        return ZLambda;
    }

    private static boolean isValidLambda(double ATest, double ZLambda){
        double ZLambdaTest = Math.floor(TWO_PI/ATest);
        ZLambdaTest = Double.isNaN(ATest) || Double.isNaN(ZLambdaTest) ? 1 : ZLambdaTest;

        if(Double.isNaN(ZLambdaTest)){
            ZLambdaTest = 1;
        }

        if(ZLambdaTest != ZLambda){
            return false;
        }

        return true;
    }

    private static boolean isValidCoordinate(double longitude) {
        return longitude > 0 && longitude <= 1;
    }

    private static int convertedCoordinate(double coordinate) {
        if(coordinate >= 0.5){
            coordinate--;
        }

        return (int) Math.rint(Units.convert(coordinate, Units.Angle.TURN,Units.Angle.T32));
    }

}
// #TODO Revoir de fond en comble la classe (fait?)
