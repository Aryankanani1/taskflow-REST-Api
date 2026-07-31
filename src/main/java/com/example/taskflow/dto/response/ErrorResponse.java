package com.example.taskflow.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Uniform error body for every failed request. The shape stays constant across
 * all error types so clients can parse one structure everywhere:
 * {@code { status, error, message, path, timestamp }}.
 */
@Getter
public class ErrorResponse {

    private final int status;               // numeric HTTP code, e.g. 404
    private final String error;             // HTTP reason phrase, e.g. "Not Found"
    private final String message;           // human-readable detail
    private final String path;              // request URI that triggered the error
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(HttpStatus status, String message, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
