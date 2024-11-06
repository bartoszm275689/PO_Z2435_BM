public class BadShapeException extends Exception {
    public BadShapeException(String message) {
        super(message);
        System.out.println(message);
    }
}
