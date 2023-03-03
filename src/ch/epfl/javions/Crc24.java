package ch.epfl.javions;

public final class Crc24 {

    public static int GENERATOR = 0xFFF409;
    private static final int INDEX = 24;

    private int[] table;

    public Crc24(int generator){
        GENERATOR = generator;
        table = Buildtable();
    }

    public int crc(byte[] message){
        int crc = 0;
        for(byte oct : message){
            int av = crc >>> INDEX;
            crc = ((crc << 8) | oct) ^ table[av-1];
        }
        crc = Bits.extractUInt(crc, 8,INDEX);
        return crc;
     }


    private static int crcBitwise(int generator, byte[] message){
        int crc = 0;
        int[] table = new int[] {0, generator};

        for (byte oct : message) {
            for (int i=7 ; i>=0 ; i--) {
                int bit = Bits.extractUInt(oct, i, 1);
                crc = ((crc << 1) | bit) ^ table[Bits.extractUInt(crc, INDEX-1, 1)];
            }
        }
        for (int i=0 ; i<24 ; i++) {
            crc = (crc << 1) ^ table[Bits.extractUInt(crc, INDEX-1, 1)];
        }

        return Bits.extractUInt(crc, 0, INDEX);
    }

    private static int[] Buildtable(){
        int[] table = new int[256];
        for(int i=0 ; i<256 ; i++){
            byte[] num = new byte[] {(byte) i};
            table[i] = crcBitwise(GENERATOR, num);
        }
        return table;
    }
}
