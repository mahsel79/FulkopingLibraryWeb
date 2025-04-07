package se.fulkopinglibraryweb.exception;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Standardized exception for controller layer errors.
 * Includes HTTP status code and structured error details.
 */
public class ControllerException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;
    private final String details;

    /**
     * Creates a new ControllerException with full details
     * @param statusCode HTTP status code (400, 404, 500 etc)
     * @param errorCode Application-specific error code
     * @param message Human-readable error message
     * @param details Technical details about the error
     */
    public ControllerException(int statusCode, String errorCode, String message, String details) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Creates a new ControllerException with default details
     * @param statusCode HTTP status code
     * @param errorCode Application error code
     * @param message Human-readable message
     */
    public ControllerException(int statusCode, String errorCode, String message) {
        this(statusCode, errorCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }
}
