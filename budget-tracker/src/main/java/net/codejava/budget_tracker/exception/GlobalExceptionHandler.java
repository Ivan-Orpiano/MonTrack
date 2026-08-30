package net.codejava.budget_tracker.exception;

import net.codejava.budget_tracker.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Transaction ID not found in the sheet -> 404. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /** @Valid failures on the request body -> 400, with a field -> message map. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields", request, fieldErrors);
    }

    /** Malformed query params, e.g. ?type=maybe instead of INCOME/EXPENSE -> 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid value '%s' for parameter '%s'".formatted(ex.getValue(), ex.getName());
        return build(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    /** Any failure reaching or parsing data from the Google Sheets API -> 502. */
    @ExceptionHandler(GoogleSheetsException.class)
    public ResponseEntity<ApiErrorResponse> handleGoogleSheets(GoogleSheetsException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, "Google Sheets service error: " + ex.getMessage(), request, null);
    }

    /** Catch-all safety net -> 500. Never leaks stack traces to the client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, null);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, HttpServletRequest request,
                                                     Map<String, String> fieldErrors) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(error);
    }
}