package org.jenmo.core.orm;

public class IllegalJpaSpiActionException extends RuntimeException {
   private static final long serialVersionUID = 4538510258241961793L;

   public IllegalJpaSpiActionException() {
      super();
   }

   public IllegalJpaSpiActionException(String message) {
      super(message);
   }

   public IllegalJpaSpiActionException(String message, Throwable cause) {
      super(message, cause);
   }

   public IllegalJpaSpiActionException(Throwable cause) {
      super(cause);
   }
}
