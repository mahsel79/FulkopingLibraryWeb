package se.fulkopinglibraryweb.exception;

public class BookServiceException extends RuntimeException {
    private final String operation;
    private final String bookId;
    private final ErrorType errorType;

    public enum ErrorType {
        NOT_FOUND,
        VALIDATION,
        DATABASE,
        CONCURRENCY,
        UNKNOWN
    }

    public BookServiceException(String message, String operation, String bookId, ErrorType errorType) {
        super(message);
        this.operation = operation;
        this.bookId = bookId;
        this.errorType = errorType;
    }

    public BookServiceException(String message, String operation, String bookId, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.bookId = bookId;
        this.errorType = errorType;
    }

    public String getOperation() {
        return operation;
    }

    public String getBookId() {
        return bookId;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
