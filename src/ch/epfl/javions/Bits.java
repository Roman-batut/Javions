package ch.epfl.javions;

public class Bits {

    private Bits(){
    }

    public static int extractUInt(long value, int start, int size){
        if(size<=0 || size >=32) {
            throw new IllegalArgumentException();
        }
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        long sl = (long)(Math.pow(2,size)-1);
        value = (value>>start) ;
        value= value & (sl);
        return (int)value;
    }

    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        value = value >> index;
        if (value % 2 == 0) {
            return false;
        } else {
            return true;
        }
    }
}

