package ch.epfl.javions.demodulation;

import java.io.*;

/**
 *  Class representing a decoder of samples
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class SamplesDecoder {

    private InputStream stream;
    private int batchSize;
    private byte[] batchtab;

    //* Constructor

    /**
     *  Constructor of a decoder of samples
     *  @param stream the stream of samples
     *  @param batchSize the size of the batch
     *  @throws IllegalArgumentException if the batch size is not positive
     *  @throws NullPointerException if the stream is null
     */
    public SamplesDecoder(InputStream stream, int batchSize){
        if(batchSize <= 0 ){
            throw new IllegalArgumentException();
        }
        if(stream.equals(InputStream.nullInputStream())){
            throw new NullPointerException();
        }

        this.stream = stream;
        this.batchSize = batchSize;
    }


    //* Methods
    
    
    /**
     *  Returns the number of samples read,
     *  changes the batch of samples to contain the read samples
     *  @param batch the batch of samples
     *  @throws IOException if an I/O error occurs
     *  @throws IllegalArgumentException if the batch size is not equal to the batch size of the decoder
     */
    public int readBatch(short[] batch) throws IOException{
        if(batch.length != batchSize){
            throw new IllegalArgumentException();
        }

        batchtab = stream.readNBytes((batchSize*2));
        stream.close();

        int lenght = batchSize*2;
        if(batchtab.length < batchSize*2){
            lenght = batchSize;
        }

        int k = 0;
        for (int i=0 ; i<lenght ; i+=2) {
            byte strong = batchtab[i+1];
            byte weak = batchtab[i];
            short fin = (short)((strong<<8|weak));
            if(fin > Math.scalb(1d,11)-1 || fin< -Math.scalb(1d,11)){
                fin -= Math.scalb(1d,11);
            }
            batch[k] = fin;
            k++;
        }

        return (int)(lenght*0.5);
    }
}
