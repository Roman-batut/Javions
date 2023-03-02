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
            int av = crc >> INDEX;
            crc = ((crc << 8) | oct) ^ table[av-1];
        }
        crc = Bits.extractUInt(crc, 8,INDEX);
        return crc;
     }


    private static int crcBitwise(int generator, byte[] message){
        byte crc = 0;
        byte[] table = new byte[]{0, (byte)generator};
        for(byte oct : message ) {
            byte bitav = 0;
            for (int i = 0; i < 8; i++) {
                byte bit = (byte) ((1000_0000 >> i) & oct);
                crc = (byte) (((crc << 1) | bit) ^ table[bitav]);
                bitav = bit;
            }
        }
        for(int i = 0; i < 24; i++) {
             byte bit = (0000_0000);
             int av = crc >> (INDEX - 1);
             crc = (byte) (((crc << 1) | bit) ^ table[av]);
        }

        return crc;
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
