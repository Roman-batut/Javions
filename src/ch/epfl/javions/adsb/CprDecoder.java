package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

public class CprDecoder {

    private final static int[] Z_PHI = new int[]{60, 59};
    private final static double[] DELTA_PHI = new double[]{(1d/Z_PHI[0]), (1d/Z_PHI[1])};

    private CprDecoder(){
    }

    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 1 || mostRecent == 0);

        //latitude
        double z_phi = Math.rint(Z_PHI[0]*y0 - Z_PHI[1]*y1);
        double[] z_phi_i = new double[]{z_phi, z_phi};
        double[] phi = new double[]{y0, y1};

        for (int i=0 ; i<2 ; i++) {
            if(z_phi<0){
                z_phi_i[i] += Z_PHI[i];
            }
            phi[i] = DELTA_PHI[i]*(z_phi_i[i]+phi[i]);
        }

//        #TODO ATTENTION C'EST EN TOUR PAS EN RADIAN OU BAILLE COMME CA
//        #TODO ASK FOR THE CASE WHERE THE LATITUDE CHANGE ZONE IF IT'S IMPIRTANTE OR NOT

        //longitude
        double[] Z_lambda = new double[2];
        double[] A = new double[]{Math.acos(1-(1-Math.cos(2*Math.PI*DELTA_PHI[0]))/(Math.cos(phi[0]*Math.cos(phi[0])))),
                Math.acos(1-(1-Math.cos(2*Math.PI*DELTA_PHI[0]))/(Math.cos(phi[1]*Math.cos(phi[1]))))};
        double[] lambda = new double[]{x0,x1};

        //exception handler
        double previous = 0;
        for (int i=0 ; i<2 ; i++) {
             if(Double.isNaN(A[i])){
                 Z_lambda[0] = 1;
             }else {
                 Z_lambda[0] = Math.floor(2*Math.PI/A[i]);
             }
             if(i == 0){
                 previous = Z_lambda[0];
             }
             if (previous != Z_lambda[0]){
                 return null;
             }
             Z_lambda[1] = Z_lambda[0]-1;
        }

        double[] Delta_lambda = new double[] {(1d/Z_PHI[0]), (1d/Z_PHI[1])};

        if(Z_lambda[0]>1){
            double z_lambda = Math.rint(Z_lambda[0]*x0 - Z_lambda[1]*x1);
            double[] z_lambda_i = new double[]{z_lambda, z_lambda};

            for (int i = 0; i < 2; i++) {
                 if(z_lambda <0){
                     z_lambda_i[i] += Z_lambda[i];
                 }

                 lambda[i] = Delta_lambda[i]*(z_lambda_i[i]+lambda[i]);
            }
        }

        //Coordonées finales

        if(phi[mostRecent] > 0.5 ){
            phi[mostRecent] = -phi[mostRecent];
        }
        phi[mostRecent] = Units.convert(phi[mostRecent], Units.Angle.TURN,Units.Angle.T32);
        if(lambda[mostRecent] > 0.5 ){
            lambda[mostRecent] = -lambda[mostRecent];
        }
        lambda[mostRecent] = Units.convert(lambda[mostRecent], Units.Angle.TURN,Units.Angle.T32);
//        #TODO LA CONVERTION EST PEUT ETRE PAS SUPER BONNE AU NIVEAU DES TOURS EST DU SIGNE
        return new GeoPos((int)lambda[mostRecent], (int)phi[mostRecent]);
    }

}
