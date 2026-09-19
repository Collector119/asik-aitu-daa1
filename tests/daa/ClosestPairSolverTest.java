package daa;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClosestPairSolverTest {

    private static final double TOLERANCE = 1e-9;

    private final Random random = new Random(4);

    @Test
    void matchesBruteForceOnSmallDatasets() {
        for (int count = 2; count <= 200; count += 17) {
            Point[] points = randomPoints(count, 1000);
            double expected = ClosestPairSolver.findClosestDistanceByBruteForce(points);
            assertEquals(expected, ClosestPairSolver.findClosestDistance(points, new Metrics()), TOLERANCE);
        }
    }

    @Test
    void matchesBruteForceOnTwoThousandPoints() {
        Point[] points = randomPoints(2000, 10000);
        double expected = ClosestPairSolver.findClosestDistanceByBruteForce(points);
        assertEquals(expected, ClosestPairSolver.findClosestDistance(points, new Metrics()), TOLERANCE);
    }

    @Test
    void handlesDuplicatePoints() {
        Point[] points = {
                new Point(1, 1), new Point(5, 5), new Point(1, 1), new Point(9, 3)
        };
        assertEquals(0.0, ClosestPairSolver.findClosestDistance(points, new Metrics()), TOLERANCE);
    }

    @Test
    void handlesPointsOnOneVerticalLine() {
        Point[] points = new Point[500];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(7, i * 2);
        }
        assertEquals(2.0, ClosestPairSolver.findClosestDistance(points, new Metrics()), TOLERANCE);
    }

    @Test
    void handlesSmallestValidInput() {
        Point[] points = {new Point(0, 0), new Point(3, 4)};
        assertEquals(5.0, ClosestPairSolver.findClosestDistance(points, new Metrics()), TOLERANCE);
    }

    @Test
    void rejectsFewerThanTwoPoints() {
        assertThrows(IllegalArgumentException.class,
                () -> ClosestPairSolver.findClosestDistance(new Point[]{new Point(0, 0)}, new Metrics()));
    }

    @Test
    void usesLogarithmicRecursionDepthOnLargeInput() {
        int count = 100000;
        Metrics metrics = new Metrics();
        ClosestPairSolver.findClosestDistance(randomPoints(count, count), metrics);
        int allowedDepth = (int) (Math.log(count) / Math.log(2)) + 5;
        assertTrue(metrics.getMaxDepth() <= allowedDepth,
                "depth " + metrics.getMaxDepth() + " exceeded " + allowedDepth);
    }

    private Point[] randomPoints(int count, int range) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new Point(random.nextDouble() * range, random.nextDouble() * range);
        }
        return points;
    }
}
