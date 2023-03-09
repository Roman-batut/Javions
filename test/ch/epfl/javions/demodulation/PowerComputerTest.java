package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PowerComputerTest {
    String directory;
    @Test
    void normalPowercomputerTest() throws IOException {
        directory = getClass().getResource("/samples.bin").getFile();
        InputStream stream = new FileInputStream(directory);
        PowerComputer s =new PowerComputer(stream, 24);
        int[] batch = new int[24];
        s.readBatch(batch);
        System.out.print(Arrays.toString(batch));

    }
    //bonne valeur mais faut décaler de trois vers la droite et puis
    //réduire la table du tableau mais prometteur
    //opti ou pas et pourquoi 10
}