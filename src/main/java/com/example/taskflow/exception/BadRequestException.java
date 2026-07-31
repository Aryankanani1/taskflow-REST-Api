package com.example.taskflow.exception;

/**
 * Thrown when a request is malformed or semantically invalid. Maps to HTTP 400.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
