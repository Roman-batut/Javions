package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 *  Class representing a window of power
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class PowerWindow {

    private final static int BATCH_SIZE = (int) Math.scalb(1d, 16);
    private PowerComputer computer;
    private int windowSize;
    private int position;
    private long sizeB;
    private int[] batchpowerOne;
    private int[] batchpowerTwo;

    //* Constructor

    /**
     *  Constructor of a power window
     *  @param stream the stream of samples
     *  @param windowSize the size of the window
     *  @throws IllegalArgumentException if the window size is not positive or greater than 2^16
     *  @throws IOException if an I/O error occurs
     *  @throws NullPointerException if the stream is null
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        Preconditions.checkArgument(!(windowSize <= 0 || windowSize>BATCH_SIZE));

        this.windowSize = windowSize;
        position = 0;
        sizeB = 0;

        batchpowerOne = new int[BATCH_SIZE];
        batchpowerTwo = new int[BATCH_SIZE];

        computer = new PowerComputer(stream, BATCH_SIZE);
        sizeB += computer.readBatch(batchpowerOne);
    }


    //* Methods


    /**
     *  Returns the size of the window
     */
    public int size(){
        return windowSize;
    }

    /**
     *  Returns the position of the window
     */
    public long position(){
        return position;
    }

    /**
     *  Returns true if the window is full
     */
    public boolean isFull(){
        return (sizeB>= windowSize+position);
    }

    /**
     *  Returns the power at the given position in the window
     *  @param i the position in the window
     *  @throws IndexOutOfBoundsException if the position is not in the window
     */
    public int get(int i){
        if(i<0 || i>=windowSize){
            throw new IndexOutOfBoundsException();
        }
        int relativepos = (position%BATCH_SIZE + i);
        if(relativepos >= BATCH_SIZE){
            return batchpowerTwo[relativepos - BATCH_SIZE];
        } else{
            return batchpowerOne[relativepos];
        }
    }

    /**
     *  Advances the window by one position, 
     *  reading a new batch if necessary
     *  @throws IOException if an I/O error occurs
     */
    public void advance() throws IOException{
        position ++;
        if(position+windowSize == sizeB){
            sizeB += computer.readBatch(batchpowerTwo);
        }
        if(position%BATCH_SIZE == 0){
            int[] tempTab = batchpowerTwo;
            batchpowerTwo = batchpowerOne;
            batchpowerOne = tempTab;
        }
    }

    /**
     *  Advances the window by the given offset, 
     *  using multiple calls to advance
     *  @param offset the offset
     *  @throws IllegalArgumentException if the offset is negative
     *  @throws IOException if an I/O error occurs
     */
    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset >= 0);

        for (int i=0 ; i<offset ; i++) {
            advance();
        }
    }
}
