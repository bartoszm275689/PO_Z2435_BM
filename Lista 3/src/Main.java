public class Main {
    public static void main(String[] args) {

        Point p1 = new Point(1, 2);
        Point p2 = new Point(4, 6);

        Calculator calc = new Calculator();

        System.out.println("Odległość między punktami: " + calc.distance(p1, p2));
        System.out.println("Odległość na osi X: " + calc.distanceX(p1, p2));
        System.out.println("Odległość na osi Y: " + calc.distanceY(p1, p2));

        Circle circle = new Circle(5);  //

        System.out.println("Pole powierzchni: " + circle.area());
        System.out.println("Obwód: " + circle.circumference());


    }
}


