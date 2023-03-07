package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private int windowSize;
    private int position;
    private PowerComputer computer;
    private int[] batchpowerOne;
    private int[] batchpowerTwo;

    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        if(windowSize <= 0 || windowSize>Math.scalb(1d,16)){
            throw new IllegalArgumentException();
        }

        this.windowSize = windowSize;
        position = 0;

        computer = new PowerComputer(stream, windowSize);
        computer.readBatch(batchpowerOne);
    }

    public int size(){
        return windowSize;
    }

    public long position(){
        return position;
    }

    public boolean isFull(){
        return (batchpowerOne.length+batchpowerTwo.length == windowSize); //Faux
    }

    public int get(int i){
        if(i<0 || i>windowSize){
            throw new IndexOutOfBoundsException();
        }

        if(i+position > batchpowerOne.length){
            return batchpowerTwo[i-batchpowerOne.length];
        }
        else{
            return batchpowerOne[i];
        }

    }

    public void advance() throws IOException{
        position++;

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
