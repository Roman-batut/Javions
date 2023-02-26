package ch.epfl.javions;

import java.util.Objects;

/**
 *  Class for bit manipulation
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public class Bits {

    //* Constructor

    /**
     * Private constructor to prevent instantiation
     */
    private Bits(){
    }


    //* Methods


    /**
     * Returns the value of the bit at the given index in the given value.
     * @param value the value to be extracted
     * @param start the index of the first bit to be extracted
     * @param size the number of bits to be extracted
     * @return the extracted value
     * @throws IndexOutOfBoundsException if the indices are not valid
     * @throws IllegalArgumentException if the size is not valid
     */
    public static int extractUInt(long value, int start, int size){
        if(size<=0 || size >=32) {
            throw new IllegalArgumentException();
        }
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        long sl = (long)(Math.pow(2,size) - 1);
        value = (value>>start) ;
        value = value & (sl);
        return (int)value;
    }

    /**
     * Tests whether the bit at the given index is set in the given value.
     * @param value the value to be tested
     * @param index the index of the bit to be tested
     * @return true if the bit at the given index is set, false otherwise
     * @throws IndexOutOfBoundsException if the index is not valid
     */
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        value = value >> index;
        if(value%2 == 0) {
            return false;
        } else {
            return true;
        }
    }
}

