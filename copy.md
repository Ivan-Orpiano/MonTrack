package com.budgettracker.exception;

/** Thrown when a requested transaction ID does not exist in the sheet. Mapped to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}