package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

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
    private final int REGUL = (int)Math.scalb(1d,11);

    //* Constructor

    /**
     *  Constructor of a decoder of samples
     *  @param stream the stream of samples
     *  @param batchSize the size of the batch
     *  @throws IllegalArgumentException if the batch size is not positive
     *  @throws NullPointerException if the stream is null
     */
    public SamplesDecoder(InputStream stream, int batchSize){
        Preconditions.checkArgument(!(batchSize <= 0 ));
        //if(batchSize <= 0 ){
        //    throw new IllegalArgumentException();
        //}
        if(stream.equals(InputStream.nullInputStream()) || stream == null){
            throw new NullPointerException();
        }

        this.stream = stream;
        this.batchSize = batchSize;
    }


    //* Methods
    
    
    /**
     *  Returns the number of samples read,
     *  changes the batch of samples to contain the extracted bytes
     *  @param batch the batch of samples
     *  @throws IOException if an I/O error occurs
     *  @throws IllegalArgumentException if the batch size is not equal to the batch size of the decoder
     */
    public int readBatch(short[] batch) throws IOException{
        Preconditions.checkArgument(batch.length == batchSize);

        batchtab = stream.readNBytes((batchSize*2));

        int length = batchSize*2;

        if(batchtab.length < batchSize*2){
            length = batchtab.length;
        }

        int k = 0;
        for (int i=0 ; i<length ; i+=2) {
            int weak =  Byte.toUnsignedInt(batchtab[i]);
            int strong = Byte.toUnsignedInt(batchtab[i+1]);

            short fin = (short)((strong<<8)|weak);
            fin -= REGUL;
            if(fin > REGUL-1 ){
                fin += REGUL;
            }
            if( fin< -REGUL){
                fin -= REGUL;
            }
            batch[k] = fin;
            k++;
        }

        return (int) (length*0.5);
    }
}
