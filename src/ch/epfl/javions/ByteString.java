package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Class for byte manipulation
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class ByteString {
    
    private byte[] byteTab;

    //* Constructor

    /**
     * Public constructor that clones the given array
     */
    public ByteString(byte[] bytes){
        byteTab = bytes.clone();
    }


    //* Methods


    /**
     * Returns a ByteString corresponding to the given string of hexadecimal characters
     * @param hexString a string of hexadecimal characters
     * @return a ByteString corresponding to the given string
     */
    public static ByteString ofHexadecimalString(String hexString){
        byte[] bytes = HexFormat.of()
                        .withUpperCase()
                        .parseHex(hexString);

        return new ByteString(bytes);
    }

    /**
     * Returns the size of the ByteString
     * @return the size of the ByteString
     */
    public int size(){
        return byteTab.length;
    }

    /**
     * Returns the byte at the given index
     * @param index the index of the byte to be returned
     * @return the byte at the given index
     * @throws IndexOutOfBoundsException if the index is not valid
     */
    public int byteAt(int index){
        if(index<0 || index>byteTab.length){
            throw new IndexOutOfBoundsException();
        }

        return (byteTab[index] & 0xFF);
    }

    /**
     * Returns the value of the bytes in the given range
     * @param fromIndex the index of the first byte to be included
     * @param toIndex the index of the last byte to be included
     * @return the value of the bytes in the given range
     * @throws IndexOutOfBoundsException if the indices are not valid
     */
    public long bytesInRange(int fromIndex, int toIndex){
        Objects.checkFromToIndex(fromIndex, toIndex, size());
        Preconditions.checkArgument((toIndex-fromIndex) <= (Long.SIZE/8));

        long bytesInRange = 0;
        for (int i=fromIndex ; i<toIndex ; i++){
            bytesInRange = (bytesInRange<<8) | byteAt(i);
        }

        return bytesInRange;
    }



    //* Object overrides
    

    /**
     * @return true if the given object is a ByteString and has the same bytes as this ByteString
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof ByteString byteS){
            return Arrays.equals(this.byteTab, byteS.byteTab);
        }

        return false;
    }

    /**
     * @return the hash code of the ByteString
     */
    @Override
    public int hashCode(){
        return Arrays.hashCode(byteTab);
    }

    /**
     * @return a hex string representation of the ByteString
     */
    @Override
    public String toString(){
        return HexFormat.of()
                .withUpperCase()
                .formatHex(byteTab);
    }
}

