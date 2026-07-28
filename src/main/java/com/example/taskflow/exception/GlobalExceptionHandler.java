package com.example.taskflow.exception;

import com.example.taskflow.dto.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Routes every exception through the same {@link ApiResponse} envelope so
 * error JSON matches success JSON. Clients parse one shape everywhere.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            CategoryAlreadyExistsException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleAlreadyExists(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            TaskNotFoundException.class,
            CategoryNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        // A DB constraint rejected the write (e.g. the unique (name, user_id) on
        // categories under a concurrent create). Report it as a conflict, not a 500.
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("resource already exists or violates a constraint"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthentication(AuthenticationException ex) {
        // Uniform 401 for any auth failure (bad password, unknown email, etc.) — a single
        // generic message so responses don't reveal whether an account exists.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("invalid email or password"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        // A path/query param couldn't be converted to the target type, e.g. an
        // invalid enum value like status=finished. List the allowed values when it's an enum.
        String message = "Invalid value '" + ex.getValue() + "' for '" + ex.getName() + "'";
        Class<?> type = ex.getRequiredType();
        if (type != null && type.isEnum()) {
            message += ". Allowed values: " + Arrays.toString(type.getEnumConstants());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        // Body is missing or can't be parsed into the target type (bad JSON, wrong
        // field type, or an invalid enum value like priority "High" vs "HIGH").
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Malformed or invalid request body"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation failed", fieldErrors));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResource(NoResourceFoundException ex) {
        // No controller mapping matched the requested URL. Report a clear 404 instead of
        // letting it fall through to the generic 500 "Something went wrong".
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("No resource found for path: " + ex.getResourcePath()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong"));
    }
}
