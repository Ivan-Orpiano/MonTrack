package net.codejava.budget_tracker.exception;

public class GoogleSheetsException extends RuntimeException {

  public GoogleSheetsException(String message, Throwable cause) {
    super(message, cause);
  }

  public GoogleSheetsException(String message) {
    super(message);
  }
    
}
