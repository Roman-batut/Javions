package ch.epfl.javions.demodulation;

import java.io.*;


public final class SamplesDecoder {

    private InputStream stream;
    private int batchSize;
    private byte[] batchtab;

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
