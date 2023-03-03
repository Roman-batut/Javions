package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import ch.epfl.javions.Crc24;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class Crc24Test {
    String a = "8D392AE499107FB5C00439035DB8";
    String b = "8D4D2286EA428867291C08EE2EC6";
    String c = "8D3950C69914B232880436BC63D3";
    String d = "8D4B17E399893E15C09C219FC014";
    String e = "8D4B18F4231445F2DB63A0DEEB82";
    String f = "8D495293F82300020049B8111203";
    @Test
    void crc() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String messageS = "8D392AE499107FB5C00439";
        String crcS = "035DB8";
        int c = Integer.parseInt(crcS, 16); // == 0x035DB8

        byte[] mAndC = HexFormat.of().parseHex(messageS + crcS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(messageS);
        assertEquals(c, crc24.crc(mOnly));
    }
}