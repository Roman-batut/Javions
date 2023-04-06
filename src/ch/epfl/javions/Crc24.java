package ch.epfl.javions;

/**
 *  Class representing a CRC24
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class Crc24 {

    public static final int GENERATOR = 0xFFF409;
    private static final int INDEX = 24;
    private static final int BYTE_RANGE = 256;
    private int[] table;

    //* Constructor

    /**
     *  Constructor of a CRC24, constructs the table
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
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(oct)) ^ table[Bits.extractUInt(crc, INDEX-Byte.SIZE, Byte.SIZE)];
        }

        for (int i=0 ; i<3 ; i++) {
            crc = (crc << Byte.SIZE) ^ table[Bits.extractUInt(crc, INDEX-Byte.SIZE, Byte.SIZE)];
        }

        return Bits.extractUInt(crc, 0, INDEX);
    }

    //* Private Methods

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

        for (int i=0 ; i<INDEX ; i++) {
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
        int[] table = new int[BYTE_RANGE];

        for(int i=0 ; i < BYTE_RANGE ; i++){
            byte[] num = new byte[] {(byte) i};
            table[i] = crcBitwise(generator, num);
        }

        return table;
    }
}
//#TODO check si la modulation est bonne ici
