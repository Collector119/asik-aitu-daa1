package daa;

public class Point {

    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceTo(Point other) {
        double differenceX = x - other.x;
        double differenceY = y - other.y;
        return Math.sqrt(differenceX * differenceX + differenceY * differenceY);
    }
}
