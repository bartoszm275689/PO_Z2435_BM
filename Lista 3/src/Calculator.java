public class Calculator {
    public double distance (Point a, Point b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }
    public double distanceX (Point a, Point b) {
        return Math.abs(b.x - a.x);
    }
    public double distanceY (Point a, Point b) {
        return Math.abs(b.y - a.y);
    }

}
