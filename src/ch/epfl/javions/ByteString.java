package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {
    private byte[] tab;
    public ByteString(byte[] bytes){
        tab = bytes.clone();
    }

    public static ByteString ofHexadecimalString(String hexString){
        HexFormat hf = HexFormat.of().withUpperCase();
        byte[] bytes = hf.parseHex(hexString);
        ByteString bytes2 = new ByteString(bytes);
        return bytes2;
    }

    public int size(){
        return tab.length;
    }

    public int byteAt(int index){
        if(index<0 || index>tab.length){
            throw new IndexOutOfBoundsException();
        }

        return (tab[index] & 0xFF);
    }

    public long bytesInRange(int fromIndex, int toIndex){
        Objects.checkFromToIndex(fromIndex, toIndex, size());
        Objects.checkFromToIndex(fromIndex, toIndex, (Long.SIZE/8));
        long l = 0;
        for (int i=fromIndex ; i<toIndex ; i++){
            l = l<<8;
            l = l | byteAt(i);
        }
        return l;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof ByteString byteS){
            if(Arrays.equals(this.tab, byteS.tab)){
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode(){
        return Arrays.hashCode(tab);
    }

    @Override
    public String toString(){
        HexFormat hf = HexFormat.of().withUpperCase();
        String string = hf.formatHex(tab);
        return string;
    }
}
