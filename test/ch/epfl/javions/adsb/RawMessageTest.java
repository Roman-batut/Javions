package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class RawMessageTest {
    String messageS = "8D392AE499107FB5C00439";
    String crcS = "035DB8";
    String debut ="8D392AE499107FB5C00439035DB8";
    byte[] mAndC = HexFormat.of().parseHex(messageS + crcS);
    byte[] mAnoC = HexFormat.of().parseHex(messageS);
    byte[] dbt = HexFormat.of().parseHex(debut);
    RawMessage test = new RawMessage(100000, new ByteString(mAndC));
    @Test
    void of() {

        assertEquals(test, RawMessage.of(100000, mAndC));
        assertNull(RawMessage.of(100000, mAnoC));
    }

    @Test
    void size() {
        byte byte0= (byte)-118;
        assertEquals(14,RawMessage.size(byte0));
    }

    @Test
    void typeCode() {
        //je peu pas creer de long snif snif
    }

    @Test
    void downLinkFormat() {
        assertEquals(141, test.downLinkFormat());
    }

    @Test
    void icaoAddress() {
        IcaoAddress nex = new IcaoAddress("392AE4");
        assertEquals(nex.string(), test.icaoAddress().string());
    }

    @Test
    void payload() {
        assertEquals(43083812132881465l, test.payload());
    }

    @Test
    void testTypeCode() {
        long type = 0b11111000000000000110010101010101010101001010101001010111l;
        assertEquals(0b11111, RawMessage.typeCode(type));
    }

    @Test
    void timeStampNs() {
    }

    @Test
    void bytes() {
    }

}