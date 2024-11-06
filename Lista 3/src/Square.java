public class Square extends Shape {
    private double side;

    public Square(double side) {
        try {
            if (side <= 0) {
                throw new BadShapeException("Bok kwadratu musi być większy niż zero.");
            } else if (side < 1) {
                throw new BadShapeExceptionTwo("Bok kwadratu musi wynosić co najmniej 1.");
            }
            this.side = side;

        } catch (BadShapeExceptionTwo exception) {
            System.out.println("Wyjątek: " + exception.getMessage());
        } catch (BadShapeException exception) {
            System.out.println("Wyjątek: " + exception.getMessage());
        }
    }

    @Override
    public double area() {
        return Math.pow(side, 2);
    }

    @Override
    public double circumference() {
        return 4 * side;
    }
}






