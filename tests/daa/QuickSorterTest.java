package daa;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickSorterTest {

    private final Random random = new Random(2);

    @Test
    void sortsRandomArrayLikeArraysSort() {
        for (int size = 1; size <= 500; size += 37) {
            int[] array = randomArray(size, Integer.MAX_VALUE);
            int[] expected = array.clone();
            Arrays.sort(expected);
            QuickSorter.sort(array, new Metrics());
            assertArrayEquals(expected, array);
        }
    }

    @Test
    void sortsSortedArray() {
        int[] array = randomArray(1000, Integer.MAX_VALUE);
        Arrays.sort(array);
        int[] expected = array.clone();
        QuickSorter.sort(array, new Metrics());
        assertArrayEquals(expected, array);
    }

    @Test
    void sortsReverseSortedArray() {
        int[] array = randomArray(1000, Integer.MAX_VALUE);
        Arrays.sort(array);
        reverse(array);
        int[] expected = array.clone();
        Arrays.sort(expected);
        QuickSorter.sort(array, new Metrics());
        assertArrayEquals(expected, array);
    }

    @Test
    void sortsDuplicateHeavyArray() {
        int[] array = randomArray(1000, 5);
        int[] expected = array.clone();
        Arrays.sort(expected);
        QuickSorter.sort(array, new Metrics());
        assertArrayEquals(expected, array);
    }

    @Test
    void sortsEmptyAndSingleElementArrays() {
        int[] empty = new int[0];
        assertDoesNotThrow(() -> QuickSorter.sort(empty, new Metrics()));
        assertArrayEquals(new int[0], empty);

        int[] single = {42};
        QuickSorter.sort(single, new Metrics());
        assertArrayEquals(new int[]{42}, single);
    }

    @Test
    void keepsRecursionDepthCloseToLogarithm() {
        int size = 100000;
        int allowedDepth = 2 * (int) (Math.log(size) / Math.log(2)) + 10;
        for (int attempt = 0; attempt < 20; attempt++) {
            Metrics metrics = new Metrics();
            QuickSorter.sort(randomArray(size, Integer.MAX_VALUE), metrics);
            assertTrue(metrics.getMaxDepth() <= allowedDepth,
                    "depth " + metrics.getMaxDepth() + " exceeded " + allowedDepth);
        }
    }

    private int[] randomArray(int size, int bound) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = bound == Integer.MAX_VALUE ? random.nextInt() : random.nextInt(bound);
        }
        return array;
    }

    private void reverse(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temporary = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temporary;
        }
    }
}
