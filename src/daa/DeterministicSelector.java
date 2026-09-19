package daa;

public class DeterministicSelector {

    private static final int GROUP_SIZE = 5;

    public static int select(int[] array, int order, Metrics metrics) {
        if (array.length == 0) {
            throw new IllegalArgumentException("array must not be empty");
        }
        if (order < 0 || order >= array.length) {
            throw new IllegalArgumentException("order must be between 0 and array length minus one");
        }
        int[] working = array.clone();
        metrics.addAllocation(array.length);
        return selectInRange(working, 0, working.length - 1, order, metrics);
    }

    private static int selectInRange(int[] array, int left, int right, int order, Metrics metrics) {
        metrics.enterRecursion();
        int result;
        if (left == right) {
            result = array[left];
        } else {
            int pivotValue = medianOfMedians(array, left, right, metrics);
            int pivotIndex = partitionAroundValue(array, left, right, pivotValue, metrics);
            if (order == pivotIndex) {
                result = array[pivotIndex];
            } else if (order < pivotIndex) {
                result = selectInRange(array, left, pivotIndex - 1, order, metrics);
            } else {
                result = selectInRange(array, pivotIndex + 1, right, order, metrics);
            }
        }
        metrics.exitRecursion();
        return result;
    }

    private static int medianOfMedians(int[] array, int left, int right, Metrics metrics) {
        int count = right - left + 1;
        if (count <= GROUP_SIZE) {
            insertionSort(array, left, right, metrics);
            return array[left + count / 2];
        }
        int medianCount = 0;
        for (int groupStart = left; groupStart <= right; groupStart += GROUP_SIZE) {
            int groupEnd = Math.min(groupStart + GROUP_SIZE - 1, right);
            insertionSort(array, groupStart, groupEnd, metrics);
            int groupMedianIndex = groupStart + (groupEnd - groupStart) / 2;
            swap(array, left + medianCount, groupMedianIndex, metrics);
            medianCount++;
        }
        return selectInRange(array, left, left + medianCount - 1, left + medianCount / 2, metrics);
    }

    private static int partitionAroundValue(int[] array, int left, int right, int pivotValue, Metrics metrics) {
        int pivotIndex = left;
        while (array[pivotIndex] != pivotValue) {
            pivotIndex++;
        }
        swap(array, pivotIndex, right, metrics);
        int smallerBound = left - 1;
        for (int i = left; i < right; i++) {
            metrics.addComparison();
            if (array[i] < pivotValue) {
                smallerBound++;
                swap(array, smallerBound, i, metrics);
            }
        }
        swap(array, smallerBound + 1, right, metrics);
        return smallerBound + 1;
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

    private static void swap(int[] array, int first, int second, Metrics metrics) {
        int temporary = array[first];
        array[first] = array[second];
        array[second] = temporary;
        metrics.addSwap();
    }
}
