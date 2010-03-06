package org.jenmo.core.repository.delete;

/**
 * Signals that a delete operation has been invoked at an illegal or inappropriate time.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class IllegalDeleteException extends RuntimeException {
   private static final long serialVersionUID = 1725942916544354778L;

   public IllegalDeleteException() {
   }

   public IllegalDeleteException(String message) {
      super(message);
   }

   public IllegalDeleteException(Throwable cause) {
      super(cause);
   }

   public IllegalDeleteException(String message, Throwable cause) {
      super(message, cause);
   }
}
