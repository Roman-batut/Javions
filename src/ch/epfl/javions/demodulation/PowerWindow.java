package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private int windowSize;
    private int position;
    private PowerComputer computer;
    private int sizeB;
    private int[] batchpowerOne;
    private int[] batchpowerTwo;

    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        if(windowSize <= 0 || windowSize>Math.scalb(1d,16)){
            throw new IllegalArgumentException();
        }

        this.windowSize = windowSize;
        position = 0;

        computer = new PowerComputer(stream, windowSize);
        sizeB = computer.readBatch(batchpowerOne);

    }

    public int size(){
        return windowSize;
    }

    public long position(){
        return position;
    }

    public boolean isFull(){
        return (sizeB>position+windowSize);
    }

    public int get(int i){
        if(i<0 || i>windowSize){
            throw new IndexOutOfBoundsException();
        }
        int relativepos = (int)(position%Math.scalb(1d, 16) +i);
        if(relativepos > batchpowerOne.length){
            return batchpowerTwo[relativepos- batchpowerOne.length];
        } else{
            return batchpowerOne[relativepos];
        }

    }

    public void advance() throws IOException{
        position ++;
        if(position+windowSize >= sizeB){
            sizeB += computer.readBatch(batchpowerTwo);
        }
        if(position%Math.scalb(1d, 16) == 0){
            batchpowerOne = batchpowerTwo;
        }
    }

    public void advanceBy(int offset) throws IOException{
        if(offset < 0){
            throw new IllegalArgumentException();
        }

        for (int i=0 ; i<offset ; i++) {
            advance();
        }
    }
}
