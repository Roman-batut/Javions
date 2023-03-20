package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {
    String directory = getClass().getResource("/samples.bin").getFile();
    InputStream stream;
    PowerWindow window;
    private static final int BATCH_SIZE = 1 << 16;
    private static final int BATCH_SIZE_BYTES = bytesForPowerSamples(BATCH_SIZE);
    private static final int STANDARD_WINDOW_SIZE = 1200;
    private static final int BIAS = 1 << 11;
    private static int bytesForPowerSamples(int powerSamplesCount) {
        return powerSamplesCount * 2 * Short.BYTES;
    }
            {
        try {
            stream = new FileInputStream(directory);
            window = new PowerWindow(stream, 600);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    void size() {
        assertEquals(600, window.size());
    }

    @Test
    void position() throws IOException{
        assertEquals(0, window.position());
        window.advance();
        assertEquals(1, window.position());
        window.advanceBy(200);
        assertEquals(201, window.position());
    }


    @Test
    void powerWindowIsFullWorks() throws IOException {
        var twoBatchesPlusOneWindowBytes =
                bytesForPowerSamples(BATCH_SIZE * 2 + STANDARD_WINDOW_SIZE);
        var twoBatchesPlusOneWindow = new byte[twoBatchesPlusOneWindowBytes];
        try (var s = new ByteArrayInputStream(twoBatchesPlusOneWindow)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(BATCH_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(BATCH_SIZE);
            assertTrue(w.isFull());

            w.advance();
            assertFalse(w.isFull());
        }
    }

    @Test
    void get() throws IOException{
        assertEquals(73, window.get(0));
        window.advanceBy(1);
        assertEquals(292, window.get(0));
        assertEquals(1370, window.get(15));
    }

    @Test
    void advance() {
    }

    @Test
    void advanceBy() {

    }

    @Test
    void testOnLargerSamples() throws IOException {
        String directory = getClass().getResource("/samples_20230304_1442.bin").getFile();
        InputStream stream = new FileInputStream(directory);
        PowerWindow pw = new PowerWindow(stream, 1200);


        while (pw.position() < 1205) {
            int j = 0;
            for (int i = 0; i < pw.size(); i++) {
                System.out.print(pw.get(i) + ", ");
                j++;
            }
            System.out.println(" ");
            System.out.println(pw.position() + " -- " + j);
            pw.advance();
        }
    }


}