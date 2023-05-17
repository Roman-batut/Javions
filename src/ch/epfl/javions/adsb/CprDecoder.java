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

    private final static int[] Z_PHI = new int[]{60, 59};
    private final static double[] DELTA_PHI = new double[]{(1d/Z_PHI[0]), (1d/Z_PHI[1])};
    private static final double TW0_PI = 2 * Math.PI;

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
        double zPhi = Math.rint(Z_PHI[1]*y0 - Z_PHI[0]*y1);
        double[] zPhiTab = new double[]{zPhi, zPhi};
        double[] phi = new double[]{y0, y1};

        for (int i=0 ; i<2 ; i++) {
            if(zPhi<0){
                zPhiTab[i] += Z_PHI[i];
            }
            phi[i] = DELTA_PHI[i] * (zPhiTab[i] + phi[i]);
        }

        //longitude calculation (lambda)
        double[] ZLambda = new double[2];
        double ANum = (1 - Math.cos(TW0_PI * DELTA_PHI[0]));

        double ADen = (Math.cos(Units.convert(phi[0], Units.Angle.TURN, Units.Angle.RADIAN)) * Math.cos(Units.convert(phi[0], Units.Angle.TURN, Units.Angle.RADIAN)));
        double A = Math.acos(1 - (ANum / ADen));

        double ATestDen = (Math.cos(Units.convert(phi[1], Units.Angle.TURN, Units.Angle.RADIAN)) * Math.cos(Units.convert(phi[1], Units.Angle.TURN, Units.Angle.RADIAN)));
        double ATest = Math.acos(1 - (ANum / ATestDen));

        double[] lambda = new double[]{x0, x1};

        //exception handler
        if(Double.isNaN(A)){
            ZLambda[0] = 1;
        }else {
            ZLambda[0] = Math.floor(TW0_PI / A);
            double ZLambdaTest = Math.floor(TW0_PI/ATest);
            if(Double.isNaN(ATest)){
                ZLambdaTest = 1;
            }
            if (ZLambdaTest != ZLambda[0]){
                return null ;
            }
            ZLambda[1] = ZLambda[0]-1;

            double[] DeltaLambda = new double[]{(1d/ZLambda[0]), (1d/ZLambda[1])};

            double zLambda = Math.rint(ZLambda[1]*x0 - ZLambda[0]*x1);
            double[] zLambdaTab = new double[]{zLambda, zLambda};

            for (int i=0 ; i<2 ; i++) {
                if(zLambda <0){
                    zLambdaTab[i] += ZLambda[i];
                }
                lambda[i] = DeltaLambda[i]*(zLambdaTab[i]+lambda[i]);
            }
        }

        //exception handler
        if((phi[mostRecent] > 1) || (phi[mostRecent] <= 0) || (lambda[mostRecent] > 1) || (lambda[mostRecent] <= 0)){
            return null;
        }

        //conversion and final coordinates
        if(phi[mostRecent] >= 0.5){
            phi[mostRecent]--;
        }
        phi[mostRecent] = Math.rint(Units.convert(phi[mostRecent], Units.Angle.TURN,Units.Angle.T32));

        if(lambda[mostRecent] >= 0.5 ){
            lambda[mostRecent]--;
        }
        lambda[mostRecent] = Math.rint(Units.convert(lambda[mostRecent], Units.Angle.TURN, Units.Angle.T32));

        return new GeoPos((int)lambda[mostRecent], (int)phi[mostRecent]);
    }
}
// #TODO Revoir de fond en comble la classe
