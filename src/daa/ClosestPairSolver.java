package daa;

import java.util.Arrays;
import java.util.Comparator;

public class ClosestPairSolver {

    private static final int BRUTE_FORCE_CUTOFF = 3;

    public static double findClosestDistance(Point[] points, Metrics metrics) {
        if (points.length < 2) {
            throw new IllegalArgumentException("at least two points are required");
        }
        Point[] sortedByX = points.clone();
        Arrays.sort(sortedByX, Comparator.comparingDouble(Point::getX));
        Point[] buffer = new Point[points.length];
        Point[] strip = new Point[points.length];
        metrics.addAllocation(3L * points.length);
        return findInRange(sortedByX, buffer, strip, 0, sortedByX.length - 1, metrics);
    }

    public static double findClosestDistanceByBruteForce(Point[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("at least two points are required");
        }
        double best = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double distance = points[i].distanceTo(points[j]);
                if (distance < best) {
                    best = distance;
                }
            }
        }
        return best;
    }

    private static double findInRange(Point[] points, Point[] buffer, Point[] strip,
                                      int left, int right, Metrics metrics) {
        metrics.enterRecursion();
        double best;
        if (right - left + 1 <= BRUTE_FORCE_CUTOFF) {
            best = closestInsideRange(points, left, right, metrics);
            sortRangeByY(points, left, right, metrics);
        } else {
            int middle = left + (right - left) / 2;
            double middleX = points[middle].getX();
            double leftBest = findInRange(points, buffer, strip, left, middle, metrics);
            double rightBest = findInRange(points, buffer, strip, middle + 1, right, metrics);
            best = Math.min(leftBest, rightBest);
            mergeByY(points, buffer, left, middle, right, metrics);
            best = checkStrip(points, strip, left, right, middleX, best, metrics);
        }
        metrics.exitRecursion();
        return best;
    }

    private static double closestInsideRange(Point[] points, int left, int right, Metrics metrics) {
        double best = Double.MAX_VALUE;
        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                metrics.addComparison();
                double distance = points[i].distanceTo(points[j]);
                if (distance < best) {
                    best = distance;
                }
            }
        }
        return best;
    }

    private static double checkStrip(Point[] points, Point[] strip, int left, int right,
                                     double middleX, double best, Metrics metrics) {
        int stripSize = 0;
        for (int i = left; i <= right; i++) {
            if (Math.abs(points[i].getX() - middleX) < best) {
                strip[stripSize] = points[i];
                stripSize++;
            }
        }
        double result = best;
        for (int i = 0; i < stripSize; i++) {
            for (int j = i + 1; j < stripSize && strip[j].getY() - strip[i].getY() < result; j++) {
                metrics.addComparison();
                double distance = strip[i].distanceTo(strip[j]);
                if (distance < result) {
                    result = distance;
                }
            }
        }
        return result;
    }

    private static void mergeByY(Point[] points, Point[] buffer, int left, int middle, int right, Metrics metrics) {
        for (int i = left; i <= right; i++) {
            buffer[i] = points[i];
        }
        int leftIndex = left;
        int rightIndex = middle + 1;
        for (int i = left; i <= right; i++) {
            if (leftIndex > middle) {
                points[i] = buffer[rightIndex];
                rightIndex++;
            } else if (rightIndex > right) {
                points[i] = buffer[leftIndex];
                leftIndex++;
            } else if (isHigher(buffer[leftIndex], buffer[rightIndex], metrics)) {
                points[i] = buffer[rightIndex];
                rightIndex++;
            } else {
                points[i] = buffer[leftIndex];
                leftIndex++;
            }
        }
    }

    private static void sortRangeByY(Point[] points, int left, int right, Metrics metrics) {
        for (int i = left + 1; i <= right; i++) {
            Point value = points[i];
            int j = i - 1;
            while (j >= left && isHigher(points[j], value, metrics)) {
                points[j + 1] = points[j];
                j--;
            }
            points[j + 1] = value;
        }
    }

    private static boolean isHigher(Point first, Point second, Metrics metrics) {
        metrics.addComparison();
        return first.getY() > second.getY();
    }
}
