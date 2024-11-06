public class Triangle extends Shape {
    private double sideA;
    private double sideB;
    private double sideC;

    public Triangle(double sideA, double sideB, double sideC) {
        try {

            if (sideA <= 0 || sideB <= 0 || sideC <= 0) {
                throw new BadShapeException("Każdy bok trójkąta musi być większy niż zero.");
            }
            if ((sideA + sideB <= sideC) || (sideA + sideC <= sideB) || (sideB + sideC <= sideA)) {
                throw new BadShapeExceptionTwo("Podane długości boków nie spełniają warunku trójkąta.");
            }

            this.sideA = sideA;
            this.sideB = sideB;
            this.sideC = sideC;

        } catch (BadShapeExceptionTwo exception) {
            System.out.println("Wyjątek: " + exception.getMessage());
        } catch (BadShapeException exception) {
            System.out.println("Wyjątek: " + exception.getMessage());
        }
    }


    @Override
    public double area() {
        double s = (sideA + sideB + sideC) / 2;
        return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
    }

    @Override
    public double circumference() {
        return sideA + sideB + sideC;
    }
}
