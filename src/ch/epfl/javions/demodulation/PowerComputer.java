package ch.epfl.javions.demodulation;

import java.io.InputStream;
import java.io.IOException;
public final class PowerComputer {

    private short[] echanP;

    private short[] batchD;

    private SamplesDecoder decoder;

    public PowerComputer(InputStream stream, int batchSize){
        if (batchSize%8 != 0 || batchSize <=0){
            throw new IllegalArgumentException();
        }

        echanP = new short[8];
        batchD = new short[batchSize*2];
        this.decoder = new SamplesDecoder(stream, batchSize*2);

    }

    public int readBatch(int[] batch) throws IOException{
        int size = decoder.readBatch(batchD);

        for (int i=0 ; i<8 ; i++){
            echanP[i] = batchD [i];
        }

        int k = 0;
        for(int i=8 ; i<size ; i+=2){
            int Pn =0;

            if(k%2 == 0){
                Pn = (echanP[6]-echanP[4]+echanP[2]-echanP[0])*(echanP[6]-echanP[4]+echanP[2]-echanP[0])
                        +(echanP[7]-echanP[5]+echanP[3]-echanP[1])*(echanP[7]-echanP[5]+echanP[3]-echanP[1]);
            }else{
                Pn = (-echanP[6]+echanP[4]-echanP[2]+echanP[0])*(-echanP[6]+echanP[4]-echanP[2]+echanP[0])
                        +(-echanP[7]+echanP[5]-echanP[3]+echanP[1])*(-echanP[7]+echanP[5]-echanP[3]+echanP[1]);
            }
            batch[k] = Pn;

            echanP[i%8] = batchD[i];
            echanP[(i+1)%8] = batchD[i+1];
            k++;
        }

        return k;
    }
}
