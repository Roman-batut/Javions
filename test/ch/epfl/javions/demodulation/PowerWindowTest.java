package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {
    String directory = getClass().getResource("/samples.bin").getFile();
    InputStream stream = new FileInputStream(directory);
    PowerWindow window = new PowerWindow(stream, 1200);

    PowerWindowTest() throws IOException {
    }

    @Test
    void size() {
        assertEquals(1200, window.size());
    }

    @Test
    void position() throws IOException{
        assertEquals(0, window.position());
        window.advanceBy(100);
        assertEquals(100, window.position());
    }

    @Test
    void isFull() {
    }

    @Test
    void get() {
    }

    @Test
    void advance() {
    }

    @Test
    void advanceBy() {
        
    }
}