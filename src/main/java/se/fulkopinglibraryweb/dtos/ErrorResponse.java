package se.fulkopinglibraryweb.dtos;

import java.time.Instant;

/**
 * Standardized error response format for API errors.
 * Contains consistent fields for all error responses.
 */
public class ErrorResponse {
    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final String details;

    public ErrorResponse(int status, String error, String message, String path, String details) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getDetails() {
        return details;
    }
}
