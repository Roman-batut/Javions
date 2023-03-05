package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
public final class PowerWindow {

    private int windowSize;
    private PowerComputer computer;
    private int[] batchpowerimp;
    private int[] batchpowerpair;

    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        if(windowSize <= 0 || windowSize>Math.scalb(1d,16)){
            throw new IllegalArgumentException();
        }

        this.windowSize = windowSize;
        computer = new PowerComputer(stream, windowSize);

    }

    public int size(){
        return 0;
    }

    public long position(){
        return 0;
    }

    public boolean isFull(){
        return (batchpowerpair.length+batchpowerimp.length == windowSize);
    }

    public int get(int i){
        return 0;
    }

    public void advance() throws IOException{

    }

    public void advanceBy(int offset) throws IOException{

    }
}
