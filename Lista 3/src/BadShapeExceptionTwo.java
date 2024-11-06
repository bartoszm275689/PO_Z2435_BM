public class BadShapeExceptionTwo  extends Exception {
  public BadShapeExceptionTwo (String message) {
    super(message);
    System.out.println(message);
  }
}