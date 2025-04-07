package se.fulkopinglibraryweb.exception;

import se.fulkopinglibraryweb.model.Media;

public class MediaServiceException extends RuntimeException {
    private final String operation;
    private final Object input;
    private final ErrorType errorType;

    public enum ErrorType {
        NOT_FOUND,
        VALIDATION,
        DATABASE,
        CONCURRENCY,
        UNKNOWN
    }

    // Existing constructors for String mediaId
    public MediaServiceException(String message, String operation, String mediaId, ErrorType errorType) {
        super(message);
        this.operation = operation;
        this.input = mediaId;
        this.errorType = errorType;
    }

    public MediaServiceException(String message, String operation, String mediaId, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.input = mediaId;
        this.errorType = errorType;
    }

    // New constructors for Media objects
    public MediaServiceException(String message, String operation, Media media, ErrorType errorType) {
        super(message);
        this.operation = operation;
        this.input = media;
        this.errorType = errorType;
    }

    public MediaServiceException(String message, String operation, Media media, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.input = media;
        this.errorType = errorType;
    }

    public String getOperation() {
        return operation;
    }

    public Object getInput() {
        return input;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
