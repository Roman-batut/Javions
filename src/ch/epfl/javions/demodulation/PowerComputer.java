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

    private final static int POWER_BATCH_SIZE = 8;
    private SamplesDecoder decoder;
    private short[] batchP;
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
        Preconditions.checkArgument(!(batchSize % POWER_BATCH_SIZE != 0 || batchSize <= 0));

        batchP = new short[POWER_BATCH_SIZE];
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
            int position = i % POWER_BATCH_SIZE;
            batchP[position] = batchD[i];
            batchP[(position+1) % POWER_BATCH_SIZE] = batchD[i+1];
            if(k % 2 == 0){
                Pn = (batchP[6] - batchP[4] + batchP[2] - batchP[0]) * (batchP[6] - batchP[4] + batchP[2] - batchP[0])
                        + (batchP[7] - batchP[5] + batchP[3] - batchP[1]) * (batchP[7] - batchP[5] + batchP[3] - batchP[1]);
            }else{
                Pn = (-batchP[6] + batchP[4] - batchP[2] + batchP[0]) * (-batchP[6] + batchP[4] - batchP[2] + batchP[0])
                        + (-batchP[7] + batchP[5] - batchP[3] + batchP[1]) * (-batchP[7] + batchP[5] - batchP[3] + batchP[1]);
            }
            batch[k] = Pn;

            k++;
        }

        return k;
    }
}