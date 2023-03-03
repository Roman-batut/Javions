package ch.epfl.javions;

/**
 *  Class representing a CRC24
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class Crc24 {

    public static final int GENERATOR = 0xFFF409;
    private static final int INDEX = 24;

    private int[] table;

    //* Constructor

    /**
     *  Constructor of a CRC24
     *  @param generator the generator of the CRC24
     */
    public Crc24(int generator){
        table = Buildtable(generator);
    }


    //* Methods


    /**
     *  Returns the CRC24 of a message, 
     *  functions byte by byte therefore faster than the crcBitwise function
     *  @param message the message
     */
    public int crc(byte[] message){
        int crc = 0;

        for (byte oct : message) {
            crc = ((crc << 8) | Byte.toUnsignedInt(oct)) ^ table[Bits.extractUInt(crc, INDEX-8, 8)];
        }

        for (int i=0 ; i<3 ; i++) {
            crc = (crc << 8) ^ table[Bits.extractUInt(crc, INDEX-8, 8)];
        }

        return Bits.extractUInt(crc, 0, INDEX);

    }

    /**
     *  Returns the CRC24 of a message, function used to build the table,
     *  functions bitwise therefore slower than the crc function
     *  @param generator the generator of the CRC24
     *  @param message the message
     */
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

    /**
     *  Returns the table of the CRC24,
     *  uses the crcBitwise function to build the table
     *  @param generator the generator of the CRC24
     */
    private static int[] Buildtable(int generator){
        int[] table = new int[256];
        for(int i=0 ; i<256 ; i++){
            byte[] num = new byte[] {(byte) i};
            table[i] = crcBitwise(GENERATOR, num);
        }

        return table;
    }
}
