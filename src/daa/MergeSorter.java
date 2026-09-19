package daa;

public class MergeSorter {

    private static final int INSERTION_SORT_CUTOFF = 16;

    public static void sort(int[] array, Metrics metrics) {
        if (array.length < 2) {
            return;
        }
        int[] buffer = new int[array.length];
        metrics.addAllocation(array.length);
        sortRange(array, buffer, 0, array.length - 1, metrics);
    }

    private static void sortRange(int[] array, int[] buffer, int left, int right, Metrics metrics) {
        metrics.enterRecursion();
        if (right - left + 1 <= INSERTION_SORT_CUTOFF) {
            insertionSort(array, left, right, metrics);
        } else {
            int middle = left + (right - left) / 2;
            sortRange(array, buffer, left, middle, metrics);
            sortRange(array, buffer, middle + 1, right, metrics);
            merge(array, buffer, left, middle, right, metrics);
        }
        metrics.exitRecursion();
    }

    private static void merge(int[] array, int[] buffer, int left, int middle, int right, Metrics metrics) {
        for (int i = left; i <= right; i++) {
            buffer[i] = array[i];
        }
        int leftIndex = left;
        int rightIndex = middle + 1;
        for (int i = left; i <= right; i++) {
            if (leftIndex > middle) {
                array[i] = buffer[rightIndex];
                rightIndex++;
            } else if (rightIndex > right) {
                array[i] = buffer[leftIndex];
                leftIndex++;
            } else if (isGreater(buffer[leftIndex], buffer[rightIndex], metrics)) {
                array[i] = buffer[rightIndex];
                rightIndex++;
            } else {
                array[i] = buffer[leftIndex];
                leftIndex++;
            }
        }
    }

    private static void insertionSort(int[] array, int left, int right, Metrics metrics) {
        for (int i = left + 1; i <= right; i++) {
            int value = array[i];
            int j = i - 1;
            while (j >= left && isGreater(array[j], value, metrics)) {
                array[j + 1] = array[j];
                metrics.addSwap();
                j--;
            }
            array[j + 1] = value;
        }
    }

    private static boolean isGreater(int first, int second, Metrics metrics) {
        metrics.addComparison();
        return first > second;
    }
}
