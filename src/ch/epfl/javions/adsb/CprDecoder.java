package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * Class representing a CPR decoder
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public class CprDecoder {

    private final static int[] Z_PHI = new int[]{60, 59};
    private final static double[] DELTA_PHI = new double[]{(1d/Z_PHI[0]), (1d/Z_PHI[1])};

    //* Constructor

    private CprDecoder(){
    }


    //* Methods


    /**
     * Returns the geographical position of an aircraft,
     * @param x0 the longitude coordinate of the first message
     * @param y0 the latitude coordinate of the first message
     * @param x1 the longitude coordinate of the second message
     * @param y1 the latitude coordinate of the second message
     * @param mostRecent the most recent message (0 or 1)
     * @throws IllegalArgumentException if the most recent message is not 0 or 1
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 1 || mostRecent == 0);

        //latitude calculation (phi)
        double z_phi = Math.rint(Z_PHI[1]*y0 - Z_PHI[0]*y1);
        double[] z_phi_i = new double[]{z_phi, z_phi};
        double[] phi = new double[]{y0, y1};

        for (int i=0 ; i<2 ; i++) {
            if(z_phi<0){
                z_phi_i[i] += Z_PHI[i];
            }
            phi[i] = DELTA_PHI[i]*(z_phi_i[i]+phi[i]);
        }

        //longitude calculation (lambda)
        double[] Z_Lambda = new double[2];
        double A = Math.acos(1-(1-Math.cos(2*Math.PI*DELTA_PHI[0]))/((Math.cos(Units.convert(phi[0], Units.Angle.TURN, Units.Angle.RADIAN))*Math.cos(Units.convert(phi[0], Units.Angle.TURN,Units.Angle.RADIAN)))));
        double A_test = Math.acos(1-((1-Math.cos(2*Math.PI*DELTA_PHI[0]))/((Math.cos(Units.convert(phi[1], Units.Angle.TURN, Units.Angle.RADIAN))*Math.cos(Units.convert(phi[1], Units.Angle.TURN,Units.Angle.RADIAN))))));

        double[] lambda = new double[]{x0,x1};

        //exception handler
        if(Double.isNaN(A)){
            Z_Lambda[0] = 1;
        }else {

            Z_Lambda[0] = Math.floor(2*Math.PI/A);
            double Z_Lambda_test = Math.floor(2*Math.PI/A_test);
            if (Z_Lambda_test != Z_Lambda[0]){
                return null ;
            }
            Z_Lambda[1] = Z_Lambda[0]-1;


            double[] Delta_lambda = new double[]{(1d/Z_Lambda[0]), (1d/Z_Lambda[1])};


            double z_lambda = Math.rint(Z_Lambda[1]*x0 - Z_Lambda[0]*x1);
            double[] z_lambda_i = new double[]{z_lambda, z_lambda};

            for (int i=0 ; i<2 ; i++) {
                if(z_lambda <0){
                    z_lambda_i[i] += Z_Lambda[i];
                }
                lambda[i] = Delta_lambda[i]*(z_lambda_i[i]+lambda[i]);
            }
        }

        //exception handler
        if(phi[mostRecent]>1 || phi[mostRecent]<=0 || lambda[mostRecent]>1 || lambda[mostRecent]<=0){
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
        lambda[mostRecent] = Math.rint(Units.convert(lambda[mostRecent], Units.Angle.TURN,Units.Angle.T32));

        return new GeoPos((int)lambda[mostRecent], (int)phi[mostRecent]);
    }
}

// #TODO Mettre au propre peut etre
