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
    void isFull() throws IOException{
        assertTrue(window.isFull());
        window.advanceBy(1);
        assertTrue(window.isFull());
        window.advanceBy(1180);
        assertFalse(window.isFull());
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
}