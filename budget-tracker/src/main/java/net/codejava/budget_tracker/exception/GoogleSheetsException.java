package net.codejava.budget_tracker.exception;
/**
 * Wraps any failure talking to the Google Sheets API (network errors, malformed
 * sheet data, auth failures, missing spreadsheet/tab, etc.). Mapped to HTTP 502.
 */ 
public class GoogleSheetsException extends RuntimeException {

  public GoogleSheetsException(String message, Throwable cause) {
    super(message, cause);
  }

  public GoogleSheetsException(String message) {
    super(message);
  }
    
}
