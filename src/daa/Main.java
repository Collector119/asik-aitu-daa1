package daa;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {

    private static final Path RESULTS_FILE = Path.of("results", "results.csv");
    private static final int DEMO_SIZE = 20;
    private static final int LARGE_SIZE = 100000;

    public static void main(String[] args) throws IOException {
        showMergeSort();
        showQuickSort();
        showDeterministicSelect();
        showClosestPair();
        showRecursionDepthOnLargeInput();
        showExperiments();
    }

    private static void showMergeSort() {
        printTitle(1, "MergeSort on a small random array");
        int[] array = Experiment.generateArray("random", DEMO_SIZE);
        int[] small = shrinkToSmallValues(array);
        System.out.println("before: " + Arrays.toString(small));
        Metrics metrics = new Metrics();
        MergeSorter.sort(small, metrics);
        System.out.println("after:  " + Arrays.toString(small));
        printMetrics(metrics);
    }

    private static void showQuickSort() {
        printTitle(2, "QuickSort on a small random array");
        int[] array = Experiment.generateArray("random", DEMO_SIZE);
        int[] small = shrinkToSmallValues(array);
        System.out.println("before: " + Arrays.toString(small));
        Metrics metrics = new Metrics();
        QuickSorter.sort(small, metrics);
        System.out.println("after:  " + Arrays.toString(small));
        printMetrics(metrics);
    }

    private static void showDeterministicSelect() {
        printTitle(3, "Deterministic Select finds order statistics");
        int[] array = shrinkToSmallValues(Experiment.generateArray("random", DEMO_SIZE));
        int[] reference = array.clone();
        Arrays.sort(reference);
        System.out.println("sorted: " + Arrays.toString(reference));
        Metrics metrics = new Metrics();
        for (int order : new int[]{0, DEMO_SIZE / 2, DEMO_SIZE - 1}) {
            metrics.reset();
            int selected = DeterministicSelector.select(array, order, metrics);
            System.out.println("order " + order + " -> " + selected
                    + " (expected " + reference[order] + ")");
        }
        printMetrics(metrics);
    }

    private static void showClosestPair() {
        printTitle(4, "Closest Pair of Points against brute force");
        Point[] points = Experiment.generatePoints(12);
        for (Point point : points) {
            System.out.printf("(%.2f, %.2f)%n", point.getX(), point.getY());
        }
        Metrics metrics = new Metrics();
        double divideAndConquer = ClosestPairSolver.findClosestDistance(points, metrics);
        double bruteForce = ClosestPairSolver.findClosestDistanceByBruteForce(points);
        System.out.printf("divide and conquer: %.6f%n", divideAndConquer);
        System.out.printf("brute force:        %.6f%n", bruteForce);
        printMetrics(metrics);
    }

    private static void showRecursionDepthOnLargeInput() {
        printTitle(5, "Recursion depth on " + LARGE_SIZE + " elements");
        Metrics mergeMetrics = new Metrics();
        MergeSorter.sort(Experiment.generateArray("random", LARGE_SIZE), mergeMetrics);
        Metrics quickMetrics = new Metrics();
        QuickSorter.sort(Experiment.generateArray("random", LARGE_SIZE), quickMetrics);
        System.out.println("MergeSort depth: " + mergeMetrics.getMaxDepth());
        System.out.println("QuickSort depth: " + quickMetrics.getMaxDepth());
        System.out.println("2 * log2(n):     " + (int) (2 * (Math.log(LARGE_SIZE) / Math.log(2))));
    }

    private static void showExperiments() throws IOException {
        printTitle(6, "Experiments");
        long start = System.nanoTime();
        Experiment.runAll(RESULTS_FILE);
        long elapsed = System.nanoTime() - start;
        System.out.println("results saved to " + RESULTS_FILE);
        System.out.printf("total time: %.1f s%n", elapsed / 1e9);
    }

    private static int[] shrinkToSmallValues(int[] array) {
        int[] small = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            small[i] = Math.floorMod(array[i], 100);
        }
        return small;
    }

    private static void printMetrics(Metrics metrics) {
        System.out.println("depth: " + metrics.getMaxDepth()
                + ", comparisons: " + metrics.getComparisons()
                + ", swaps: " + metrics.getSwaps()
                + ", allocations: " + metrics.getAllocations());
    }

    private static void printTitle(int number, String title) {
        System.out.println();
        System.out.println("=== " + number + ". " + title + " ===");
    }
}
