public class BadShapeException extends RuntimeException {
    public BadShapeException(String message) {
      super(message);
    }
     @Override
     public String toString() {
       return "Błąd: " + getMessage();
     }
  }

