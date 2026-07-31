package com.example.taskflow.exception;

/**
 * Thrown when creating a resource that already exists. Maps to HTTP 409.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
