package daa;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeterministicSelectorTest {

    private final Random random = new Random(3);

    @Test
    void matchesSortedArrayOnHundredRandomTests() {
        for (int test = 0; test < 100; test++) {
            int size = 1 + random.nextInt(300);
            int[] array = randomArray(size, Integer.MAX_VALUE);
            int[] expected = array.clone();
            Arrays.sort(expected);
            int order = random.nextInt(size);
            assertEquals(expected[order], DeterministicSelector.select(array, order, new Metrics()));
        }
    }

    @Test
    void findsMinimumAndMaximum() {
        int[] array = randomArray(1000, Integer.MAX_VALUE);
        int[] expected = array.clone();
        Arrays.sort(expected);
        assertEquals(expected[0], DeterministicSelector.select(array, 0, new Metrics()));
        assertEquals(expected[999], DeterministicSelector.select(array, 999, new Metrics()));
    }

    @Test
    void worksOnSingleElementArray() {
        assertEquals(42, DeterministicSelector.select(new int[]{42}, 0, new Metrics()));
    }

    @Test
    void doesNotModifyInputArray() {
        int[] array = randomArray(200, Integer.MAX_VALUE);
        int[] copy = array.clone();
        DeterministicSelector.select(array, 100, new Metrics());
        assertEquals(Arrays.toString(copy), Arrays.toString(array));
    }

    @Test
    void rejectsEmptyArrayAndOrderOutOfRange() {
        assertThrows(IllegalArgumentException.class,
                () -> DeterministicSelector.select(new int[0], 0, new Metrics()));
        assertThrows(IllegalArgumentException.class,
                () -> DeterministicSelector.select(new int[]{1, 2, 3}, 3, new Metrics()));
        assertThrows(IllegalArgumentException.class,
                () -> DeterministicSelector.select(new int[]{1, 2, 3}, -1, new Metrics()));
    }

    private int[] randomArray(int size, int bound) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = bound == Integer.MAX_VALUE ? random.nextInt() : random.nextInt(bound);
        }
        return array;
    }
}
