package daa;

import java.util.Random;

public class QuickSorter {

    private static final Random RANDOM = new Random();

    public static void sort(int[] array, Metrics metrics) {
        if (array.length < 2) {
            return;
        }
        sortRange(array, 0, array.length - 1, metrics);
    }

    private static void sortRange(int[] array, int left, int right, Metrics metrics) {
        metrics.enterRecursion();
        while (left < right) {
            int pivotIndex = partition(array, left, right, metrics);
            if (pivotIndex - left < right - pivotIndex) {
                sortRange(array, left, pivotIndex - 1, metrics);
                left = pivotIndex + 1;
            } else {
                sortRange(array, pivotIndex + 1, right, metrics);
                right = pivotIndex - 1;
            }
        }
        metrics.exitRecursion();
    }

    private static int partition(int[] array, int left, int right, Metrics metrics) {
        int randomIndex = left + RANDOM.nextInt(right - left + 1);
        swap(array, randomIndex, right, metrics);
        int pivot = array[right];
        int smallerBound = left - 1;
        for (int i = left; i < right; i++) {
            metrics.addComparison();
            if (array[i] <= pivot) {
                smallerBound++;
                swap(array, smallerBound, i, metrics);
            }
        }
        swap(array, smallerBound + 1, right, metrics);
        return smallerBound + 1;
    }

    private static void swap(int[] array, int first, int second, Metrics metrics) {
        int temporary = array[first];
        array[first] = array[second];
        array[second] = temporary;
        metrics.addSwap();
    }
}
