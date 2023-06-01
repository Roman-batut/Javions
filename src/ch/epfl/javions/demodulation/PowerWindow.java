package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 *  Class representing a window of power
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class PowerWindow {

    private final PowerComputer computer;
    private final int windowSize;
    private int position;
    private long sizeB;
    private int[] batchPowerOne;
    private int[] batchPowerTwo;

    //* Constants

    private final static int BATCH_SIZE = (1 << 16);

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
        Preconditions.checkArgument(!(windowSize <= 0 || windowSize > BATCH_SIZE));

        this.windowSize = windowSize;
        position = 0;
        sizeB = 0;

        batchPowerOne = new int[BATCH_SIZE];
        batchPowerTwo = new int[BATCH_SIZE];

        computer = new PowerComputer(stream, BATCH_SIZE);
        sizeB += computer.readBatch(batchPowerOne);
    }

    //* Getters

    /**
     * @return  the size of the window
     */
    public int size(){
        return windowSize;
    }

    /**
     * @return the position of the window
     */
    public long position(){
        return position;
    }


    //* Methods

    /**
     * @return true if the window is full
     */
    public boolean isFull(){
        return (sizeB >= windowSize + position);
    }

    /**
     *  Getter of the number at a given value
     *  @param i the position in the window
     *  @throws IndexOutOfBoundsException if the position is not in the window
     *  @return the power at the given position in the window
     */
    public int get(int i){
        Objects.checkIndex(i, windowSize);

        int relativepos = (position % BATCH_SIZE + i);
        if(relativepos >= BATCH_SIZE){
            return batchPowerTwo[relativepos - BATCH_SIZE];
        } else{
            return batchPowerOne[relativepos];
        }
    }

    /**
     *  Advances the window by one position, 
     *  reading a new batch if necessary
     *  @throws IOException if an I/O error occurs
     */
    public void advance() throws IOException{
        position++;
        if(position + windowSize == sizeB){
            sizeB += computer.readBatch(batchPowerTwo);
        }

        if(position % BATCH_SIZE == 0){
            int[] tempTab = batchPowerTwo;
            batchPowerTwo = batchPowerOne;
            batchPowerOne = tempTab;
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
