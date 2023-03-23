package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AirbornePositionMessageTest{
    @Test
    void timeStampNs() {
    }

    @Test
    void icaoAddress() throws IOException{
        RawMessage[] message = new RawMessage[6];
        AirbornePositionMessage[] mess = new AirbornePositionMessage[6];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                if(l.timeStampNs() == 75898000||l.timeStampNs()==116538700||l.timeStampNs()==138560100
                        ||l.timeStampNs()==208135700||l.timeStampNs()==233069800|| l.timeStampNs()==1499146900){
                    message[k] = l;
                    k++;
                }
                if (k ==6){
                    break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            mess[i] = AirbornePositionMessage.of(message[i]);
        }
    assertEquals("495299", mess[0].icaoAddress().string());
    assertEquals("4241A9", mess[1].icaoAddress().string());
    assertEquals("4D2228", mess[2].icaoAddress().string());
    assertEquals("4D029F", mess[3].icaoAddress().string());
    assertEquals("3C6481", mess[4].icaoAddress().string());
}

    @Test
    void of()throws IOException{
        RawMessage[] message = new RawMessage[6];
        AirbornePositionMessage[] mess = new AirbornePositionMessage[6];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                if(l.timeStampNs() == 75898000||l.timeStampNs()==116538700||l.timeStampNs()==138560100
                        ||l.timeStampNs()==208135700||l.timeStampNs()==233069800|| l.timeStampNs()==1499146900){
                    message[k] = l;
                    k++;
                }
                if (k ==6){
                    break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            mess[i] = AirbornePositionMessage.of(message[i]);
        }
       assertEquals(75898000,mess[0].timeStampNs());

    }

    @Test
    void altitude() throws IOException{
        RawMessage[] message = new RawMessage[6];
        AirbornePositionMessage[] mess = new AirbornePositionMessage[6];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                if(l.timeStampNs() == 75898000||l.timeStampNs()==116538700||l.timeStampNs()==138560100
                        ||l.timeStampNs()==208135700||l.timeStampNs()==233069800|| l.timeStampNs()==1499146900){
                    message[k] = l;
                    k++;
                }
                if (k ==6){
                    break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            mess[i] = AirbornePositionMessage.of(message[i]);
        }
        assertEquals(10546.08, mess[0].altitude());
        assertEquals(1303.02, mess[1].altitude());
        assertEquals(10972.800000000001, mess[2].altitude());
        assertEquals(4244.34, mess[3].altitude());
        assertEquals(10370.82, mess[4].altitude());
        RawMessage msgra =RawMessage.of( 100,HexFormat.of().parseHex("8D39203559B225F07550ADBE328F"));
        AirbornePositionMessage msg = AirbornePositionMessage.of(msgra);
        System.out.println(msg);
        RawMessage msgra1 =RawMessage.of( 100,HexFormat.of().parseHex("8DAE02C85864A5F5DD4975A1A3F5"));
        AirbornePositionMessage msg1 = AirbornePositionMessage.of(msgra1);
        System.out.println(msg1);
    }

    @Test
    void parity() throws IOException{
        RawMessage[] message = new RawMessage[6];
        AirbornePositionMessage[] mess = new AirbornePositionMessage[6];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                if(l.timeStampNs() == 75898000||l.timeStampNs()==116538700||l.timeStampNs()==138560100
                        ||l.timeStampNs()==208135700||l.timeStampNs()==233069800|| l.timeStampNs()==1499146900){
                    message[k] = l;
                    k++;
                }
                if (k ==6){
                    break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            mess[i] = AirbornePositionMessage.of(message[i]);
        }
        assertEquals(0, mess[0].parity());
        assertEquals(0, mess[1].parity());
        assertEquals(1, mess[2].parity());
        assertEquals(0, mess[3].parity());
        assertEquals(0, mess[4].parity());
    }

    @Test
    void x() throws IOException{
        RawMessage[] message = new RawMessage[6];
        AirbornePositionMessage[] mess = new AirbornePositionMessage[6];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                if(l.timeStampNs() == 75898000||l.timeStampNs()==116538700||l.timeStampNs()==138560100
                        ||l.timeStampNs()==208135700||l.timeStampNs()==233069800|| l.timeStampNs()==1499146900){
                    message[k] = l;
                    k++;
                }
                if (k ==6){
                    break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            mess[i] = AirbornePositionMessage.of(message[i]);
        }
        assertEquals(0.6867904663085938, mess[0].x());
        assertEquals(0.702667236328125, mess[1].x());
        assertEquals(0.6243515014648438, mess[2].x());
        assertEquals(0.747222900390625, mess[3].x());
        assertEquals(0.8674850463867188, mess[4].x());
    }

    @Test
    void y() throws IOException{
        RawMessage[] message = new RawMessage[6];
        AirbornePositionMessage[] mess = new AirbornePositionMessage[6];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                if(l.timeStampNs() == 75898000||l.timeStampNs()==116538700||l.timeStampNs()==138560100
                        ||l.timeStampNs()==208135700||l.timeStampNs()==233069800|| l.timeStampNs()==1499146900){
                    message[k] = l;
                    k++;
                }
                if (k ==6){
                    break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            mess[i] = AirbornePositionMessage.of(message[i]);
        }
        assertEquals(0.7254638671875, mess[0].y());
        assertEquals(0.7131423950195312, mess[1].y());
        assertEquals(0.4921417236328125, mess[2].y());
        assertEquals(0.7342300415039062, mess[3].y());
        assertEquals(0.7413406372070312, mess[4].y());
    }
    @Test
    void alltherest() throws IOException{
        RawMessage[] message = new RawMessage[384];
        AirbornePositionMessage[] mess = new AirbornePositionMessage[137];
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try(InputStream s = new FileInputStream(directory)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int k =0;
            for (int i = 0; i < 384; i++) {
                RawMessage l = d.nextMessage();
                message[i] = l;

                if ((message[i].typeCode()>=9 && message[i].typeCode()<=18) || (message[i].typeCode()>=20 && message[i].typeCode()<=22)){
                    mess[k] = AirbornePositionMessage.of(message[i]);
                    k++;
                }

            }
            for (int i = 0; i < k; i++) {
                System.out.println(mess[i]);
            }
            System.out.println(k);
        }
    }


}
