package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javions.GeoPos;
class CprDecoderTest {

    @Test
    void decodePosition() {
    }

    @Test
    void CprDecoderNonTrivialTest() {
        double x0 = Math.scalb(111600d, -17);
        double y0 = Math.scalb(94445d, -17);
        double x1 = Math.scalb(108865d, -17);
        double y1 = Math.scalb(77558d, -17);
        GeoPos p = CprDecoder.decodePosition(x0, y0, x1, y1, 0);
        System.out.println("Longitude T32:" + p.longitudeT32());
        System.out.println("Latitude T32:" + p.latitudeT32());
        System.out.println(p);
        /*
         * Should be :
         * Longitude
         * T32 : 89192898
         * Degrees : 7.476062346249819
         *
         * Latitude
         * T32 : 552659081
         * Degrees : 46.323349038138986
         * */
    }

    @Test
    void testVeryCloseValues() {
        GeoPos pos = CprDecoder.decodePosition(0.62, 0.42, 0.6200000000000000001, 0.4200000000000000001, 0);
        System.out.println(pos);
    }

    @Test
    void testAnotherStuff() {
        GeoPos pos = CprDecoder.decodePosition(0, 0.3, 0, 0, 0);
        System.out.println(pos);
    }

    @Test
    void testParityChange() {
        GeoPos pos0 = CprDecoder.decodePosition(0.3,0.3,0.3,0.3,0);
        System.out.println(pos0);

        GeoPos pos1 = CprDecoder.decodePosition(0.3,0.3,0.3,0.3,1);
        System.out.println(pos1);
    }

}