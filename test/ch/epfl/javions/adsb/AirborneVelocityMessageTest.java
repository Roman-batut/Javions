package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class AirborneVelocityMessageTest {

    @Test
    void timeStampNs() {
    }

    @Test
    void icaoAddress() {
    }

    @Test
    void of() {
    }
    @Test
    void st1(){
        String d = "8D485020994409940838175B284F";
        byte[] a = HexFormat.of().parseHex(d);
        RawMessage l = new RawMessage(0, new ByteString(a));
        System.out.println(AirborneVelocityMessage.of(l));
        String c = "8D485020994409940838175B284F";
        byte[] b = HexFormat.of().parseHex(c);
        RawMessage m = new RawMessage(0, new ByteString(b));
        System.out.println(AirborneVelocityMessage.of(m));
    }
    @Test
    void st2(){

    }
    @Test
    void st4(){
        String c = "8DA05F219B06B6AF189400CBC33F";
        byte[] b = HexFormat.of().parseHex(c);
        RawMessage m = new RawMessage(0, new ByteString(b));
        System.out.println(AirborneVelocityMessage.of(m));
    }


    @Test
    void speed() {
    }

    @Test
    void trackOrHeading() {
    }
}