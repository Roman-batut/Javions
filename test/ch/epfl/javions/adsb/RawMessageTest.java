package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RawMessageTest {

    @Test
    void of() {
    }

    @Test
    void size() {
        //trop for
        byte byte0= (byte)10001000;
        assertEquals(14,RawMessage.size(byte0));
    }

    @Test
    void typeCode() {
    }

    @Test
    void downLinkFormat() {
    }

    @Test
    void icaoAddress() {
    }

    @Test
    void payload() {
    }

    @Test
    void testTypeCode() {
    }

    @Test
    void timeStampNs() {
    }

    @Test
    void bytes() {
    }
}