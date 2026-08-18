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
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /** @Valid failues on the request body -> 400 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map <String, String> fieldErrors = new LinkedHashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
        fieldErrors.put(error.getField(), error.getDefaultMessage());
    }
    return build(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields", request, fieldErrors);
}


    @ExceptionHandler(MethodArgumentMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch (MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid value '' "
    }



}
