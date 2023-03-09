package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {

    @Test
    void normalSampledecoderTest() throws IOException {
        String directory = getClass().getResource("/samples.bin").getFile();
        InputStream stream = new FileInputStream(directory);
        SamplesDecoder s =new SamplesDecoder(stream, 10);
        short[] batch = new short[10];
        s.readBatch(batch);
        System.out.print(Arrays.toString(batch));
        assertEquals(1,1);
    }

    //changemetn réaliser pour mettre les valeurs entre -2048 et 2047 avec un if a voir si c'est bien
}