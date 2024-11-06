public class Circle extends Shape {
        private double radius;

        public Circle(double radius) {
                try {
                        if (radius <= 0) {
                                throw new BadShapeException("Promień musi być większy niż zero.");
                        } else if (radius < 1) {
                                throw new BadShapeExceptionTwo("Promień musi wynosić co najmniej 1.");
                        }
                        this.radius = radius;

                } catch (BadShapeExceptionTwo exception) {
                        System.out.println("Wyjątek: " + exception.getMessage());
                } catch (BadShapeException exception) {
                        System.out.println("Wyjątek: " + exception.getMessage());
                }
        }

        public double area() {
                return Math.PI * Math.pow(radius, 2);
        }

        public double circumference() {
                return 2 * Math.PI * radius;
        }
}
