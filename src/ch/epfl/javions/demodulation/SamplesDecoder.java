package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.*;
import java.util.Objects;

/**
 *  Class representing a decoder of samples
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class SamplesDecoder {

    private final static int REGUL = (1 << 11);
    private final InputStream stream;
    private final int batchSize;


    //* Constructor

    /**
     *  Constructor of a decoder of samples
     *  @param stream the stream of samples
     *  @param batchSize the size of the batch
     *  @throws IllegalArgumentException if the batch size is not positive
     *  @throws NullPointerException if the stream is null
     */
    public SamplesDecoder(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize > 0);
        Objects.requireNonNull(stream);

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
     *  @return return the number of batch read
     */
    public int readBatch(short[] batch) throws IOException{
        Preconditions.checkArgument(batch.length == batchSize);

        byte[] batchtab = new byte[batchSize * 2];
        int bytesRead = stream.readNBytes(batchtab, 0, batchSize * 2);

        int k = 0;
        for (int i = 0; i < bytesRead; i += Short.BYTES) {
            int weak = Byte.toUnsignedInt(batchtab[i]);
            int strong = Byte.toUnsignedInt(batchtab[i + 1]);

            short fin = (short)((strong << Byte.SIZE) | weak);
            fin -= REGUL;
            batch[k] = fin;
            k++;
        }

        return bytesRead / Short.BYTES;
    }
}
