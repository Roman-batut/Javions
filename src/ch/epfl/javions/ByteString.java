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

    private final static HexFormat UPPERCASE_FORMAT = HexFormat.of().withUpperCase();
    private final byte[] byteTab;

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
        byte[] bytes = UPPERCASE_FORMAT.parseHex(hexString);
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
        Objects.checkIndex(index, byteTab.length);

        return Byte.toUnsignedInt(byteTab[index]);
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
            bytesInRange = (bytesInRange<< Byte.SIZE) | byteAt(i);
        }

        return bytesInRange;
    }



    //* Object overrides
    

    /**
     * @return true if the given object is a ByteString and has the same bytes as this ByteString
     */
    @Override
    public boolean equals(Object obj){
        return (obj instanceof ByteString byteS) &&
                (Arrays.equals(this.byteTab, byteS.byteTab));
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
        return UPPERCASE_FORMAT.formatHex(byteTab);
    }
}

