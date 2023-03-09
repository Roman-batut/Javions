package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {
    String directory = getClass().getResource("/samples.bin").getFile();
    InputStream stream;
    PowerWindow window;

            {
        try {
            stream = new FileInputStream(directory);
            window = new PowerWindow(stream, 16);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    void size() {
        assertEquals(16, window.size());
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
    void isFull() throws IOException{
        assertTrue(window.isFull());
        window.advanceBy(1184);
        assertTrue(window.isFull());
        window.advanceBy(2);
        assertFalse(window.isFull());
    }

    @Test
    void get() throws IOException{
        assertEquals(73, window.get(0));
        window.advanceBy(1);
        assertEquals(1370, window.get(16));
    }

    @Test
    void advance() {
    }

    @Test
    void advanceBy() {

    }
}