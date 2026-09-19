package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public class Experiment {

    private static final String RANDOM_INPUT = "random";
    private static final String SORTED_INPUT = "sorted";
    private static final String REVERSE_SORTED_INPUT = "reverse_sorted";
    private static final String DUPLICATE_HEAVY_INPUT = "duplicate_heavy";

    private static final String[] INPUT_TYPES = {
            RANDOM_INPUT, SORTED_INPUT, REVERSE_SORTED_INPUT, DUPLICATE_HEAVY_INPUT
    };

    private static final int[] ARRAY_SIZES = {1000, 5000, 10000, 50000, 100000, 200000};
    private static final int[] POINT_COUNTS = {1000, 2000, 5000, 10000, 50000, 100000};
    private static final int DUPLICATE_VALUE_RANGE = 10;
    private static final int WARMUP_RUNS = 5;
    private static final int WARMUP_SIZE = 20000;
    private static final int MEASURED_RUNS = 5;

    private static final Random RANDOM = new Random(42);

    public static void runAll(Path resultsFile) throws IOException {
        warmUpJvm();
        Files.createDirectories(resultsFile.getParent());
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(resultsFile))) {
            writer.println("algorithm,input_type,n,time_ns,max_depth,comparisons,swaps,allocations");
            for (int size : ARRAY_SIZES) {
                for (String inputType : INPUT_TYPES) {
                    measureSorter("MergeSort", inputType, size, writer);
                    measureSorter("QuickSort", inputType, size, writer);
                }
                measureSelector(size, writer);
            }
            for (int count : POINT_COUNTS) {
                measureClosestPair(count, writer);
            }
        }
    }

    private static void warmUpJvm() {
        for (int run = 0; run < WARMUP_RUNS; run++) {
            int[] array = generateArray(RANDOM_INPUT, WARMUP_SIZE);
            MergeSorter.sort(array.clone(), new Metrics());
            QuickSorter.sort(array.clone(), new Metrics());
            DeterministicSelector.select(array, WARMUP_SIZE / 2, new Metrics());
            ClosestPairSolver.findClosestDistance(generatePoints(WARMUP_SIZE), new Metrics());
        }
    }

    private static void measureSorter(String algorithm, String inputType, int size, PrintWriter writer) {
        long[] times = new long[MEASURED_RUNS];
        Metrics metrics = new Metrics();
        int[] original = generateArray(inputType, size);
        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            int[] data = original.clone();
            metrics.reset();
            long start = System.nanoTime();
            if (algorithm.equals("MergeSort")) {
                MergeSorter.sort(data, metrics);
            } else {
                QuickSorter.sort(data, metrics);
            }
            long elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                times[run - WARMUP_RUNS] = elapsed;
            }
        }
        writeRow(writer, algorithm, inputType, size, median(times), metrics);
    }

    private static void measureSelector(int size, PrintWriter writer) {
        long[] times = new long[MEASURED_RUNS];
        Metrics metrics = new Metrics();
        int[] original = generateArray(RANDOM_INPUT, size);
        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            int[] data = original.clone();
            metrics.reset();
            long start = System.nanoTime();
            DeterministicSelector.select(data, size / 2, metrics);
            long elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                times[run - WARMUP_RUNS] = elapsed;
            }
        }
        writeRow(writer, "DeterministicSelect", RANDOM_INPUT, size, median(times), metrics);
    }

    private static void measureClosestPair(int count, PrintWriter writer) {
        long[] times = new long[MEASURED_RUNS];
        Metrics metrics = new Metrics();
        Point[] points = generatePoints(count);
        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            metrics.reset();
            long start = System.nanoTime();
            ClosestPairSolver.findClosestDistance(points, metrics);
            long elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                times[run - WARMUP_RUNS] = elapsed;
            }
        }
        writeRow(writer, "ClosestPair", RANDOM_INPUT, count, median(times), metrics);
    }

    private static void writeRow(PrintWriter writer, String algorithm, String inputType,
                                 int size, long timeNanoseconds, Metrics metrics) {
        writer.println(algorithm + "," + inputType + "," + size + "," + timeNanoseconds + ","
                + metrics.getMaxDepth() + "," + metrics.getComparisons() + ","
                + metrics.getSwaps() + "," + metrics.getAllocations());
    }

    public static int[] generateArray(String inputType, int size) {
        int[] array = new int[size];
        if (inputType.equals(DUPLICATE_HEAVY_INPUT)) {
            for (int i = 0; i < size; i++) {
                array[i] = RANDOM.nextInt(DUPLICATE_VALUE_RANGE);
            }
            return array;
        }
        for (int i = 0; i < size; i++) {
            array[i] = RANDOM.nextInt();
        }
        if (inputType.equals(SORTED_INPUT)) {
            Arrays.sort(array);
        }
        if (inputType.equals(REVERSE_SORTED_INPUT)) {
            Arrays.sort(array);
            reverse(array);
        }
        return array;
    }

    public static Point[] generatePoints(int count) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new Point(RANDOM.nextDouble() * count, RANDOM.nextDouble() * count);
        }
        return points;
    }

    private static void reverse(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temporary = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temporary;
        }
    }

    private static long median(long[] values) {
        long[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }
}
