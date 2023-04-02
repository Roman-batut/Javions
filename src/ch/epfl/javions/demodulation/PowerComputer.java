package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.InputStream;
import java.io.IOException;

/**
 *  Class representing a power computer
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class PowerComputer {

    private final static int POWER_BATCH = 8;
    private SamplesDecoder decoder;
    private short[] echanP;
    private short[] batchD;

    //* Constructor

    /**
     *  Constructor of a power computer
     *  @param stream the stream of samples
     *  @param batchSize the size of the batch
     *  @throws IllegalArgumentException if the batch size is not positive or cannot be divided by 8
     *  @throws NullPointerException if the stream is null
     */
    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(!(batchSize%POWER_BATCH != 0 || batchSize <= 0));

        echanP = new short[POWER_BATCH];
        batchD = new short[batchSize*2];
        this.decoder = new SamplesDecoder(stream, batchSize*2);
    }


    //* Methods


    /**
     *  Returns the number of powers computed,
     *  changes the batch of powers to contain the computed powers
     *  @param batch the batch of powers
     *  @throws IOException if an I/O error occurs
     *  @throws IllegalArgumentException if the batch size is not equal to the batch size of the decoder
     */
    public int readBatch(int[] batch) throws IOException{
        int size = decoder.readBatch(batchD);

        int k = 0;
        for(int i=0 ; i<size ; i+=2){
            int Pn;
            int position = i%POWER_BATCH;
            echanP[position] = batchD[i];
            echanP[(position+1)%POWER_BATCH] = batchD[i+1];
            if(k%2 == 0){
                Pn = (echanP[6]-echanP[4]+echanP[2]-echanP[0])*(echanP[6]-echanP[4]+echanP[2]-echanP[0])
                        +(echanP[7]-echanP[5]+echanP[3]-echanP[1])*(echanP[7]-echanP[5]+echanP[3]-echanP[1]);
            }else{
                Pn = (-echanP[6]+echanP[4]-echanP[2]+echanP[0])*(-echanP[6]+echanP[4]-echanP[2]+echanP[0])
                        +(-echanP[7]+echanP[5]-echanP[3]+echanP[1])*(-echanP[7]+echanP[5]-echanP[3]+echanP[1]);
            }
            batch[k] = Pn;

            k++;
        }

        return k;
    }
}
// #TODO check ici pour la consatnte et on fait quoi avec le tableau echamP ? changer le nom aussi peut etre
