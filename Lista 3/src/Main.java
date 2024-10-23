public class Main {
    public static void main(String[] args) {

        Calculator calculator = new Calculator();

        Shape square = new Square(4.0);
        System.out.println("Pole kwadratu: " + square.area());
        System.out.println("Obwód kwadratu: " + square.circumference());

        Shape triangle = new Triangle(3.0, 4.0, 5.0);
        System.out.println("Pole trójkąta: " + triangle.area());
        System.out.println("Obwód trójkąta: " + triangle.circumference());

        Shape circle = new Circle(5.0);
        System.out.println("Pole koła: " + circle.area());
        System.out.println("Obwód koła: " + circle.circumference());

        double totalarea = calculator.calculatearea(square, triangle);
        System.out.println("Suma pól: " + totalarea);

        totalarea = calculator.calculatearea(circle, square);
        System.out.println("Suma pól: " + totalarea);
    }
}




