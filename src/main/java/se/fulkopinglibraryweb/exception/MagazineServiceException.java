package se.fulkopinglibraryweb.exception;

public class MagazineServiceException extends RuntimeException {
    private final String operation;
    private final Object input;
    private final ErrorType errorType;

    public MagazineServiceException(String operation, Object input, ErrorType errorType, String message) {
        super(message);
        this.operation = operation;
        this.input = input;
        this.errorType = errorType;
    }

    public MagazineServiceException(String operation, Object input, ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.input = input;
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

    public enum ErrorType {
        NOT_FOUND,
        INVALID_INPUT,
        DATABASE_ERROR,
        CONCURRENCY_CONFLICT,
        UNAUTHORIZED
    }
}
