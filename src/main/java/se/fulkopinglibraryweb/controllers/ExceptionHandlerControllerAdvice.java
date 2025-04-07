package se.fulkopinglibraryweb.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import se.fulkopinglibraryweb.dtos.ErrorResponse;
import se.fulkopinglibraryweb.exception.ControllerException;
import se.fulkopinglibraryweb.exception.BookServiceException;
import se.fulkopinglibraryweb.exception.MediaServiceException;
import se.fulkopinglibraryweb.exception.MagazineServiceException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

    @ExceptionHandler(ControllerException.class)
    public ResponseEntity<ErrorResponse> handleControllerException(
            ControllerException ex, 
            HttpServletRequest request) {
        
        logger.error("Controller exception occurred: {} - {} (status: {})", 
            ex.getErrorCode(), ex.getMessage(), ex.getStatusCode(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getStatusCode(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getDetails()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(BookServiceException.class)
    public ResponseEntity<ErrorResponse> handleBookServiceException(
            BookServiceException ex,
            HttpServletRequest request) {
        
        logger.error("Book service exception occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "BOOK_SERVICE_ERROR",
            "Book service operation failed",
            request.getRequestURI(),
            ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MediaServiceException.class)
    public ResponseEntity<ErrorResponse> handleMediaServiceException(
            MediaServiceException ex,
            HttpServletRequest request) {
        
        logger.error("Media service exception occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "MEDIA_SERVICE_ERROR",
            "Media service operation failed",
            request.getRequestURI(),
            ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MagazineServiceException.class)
    public ResponseEntity<ErrorResponse> handleMagazineServiceException(
            MagazineServiceException ex,
            HttpServletRequest request) {
        
        logger.error("Magazine service exception occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "MAGAZINE_SERVICE_ERROR",
            "Magazine service operation failed",
            request.getRequestURI(),
            ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            request.getRequestURI(),
            ex.getMessage()
        );

        return new ResponseEntity<>(
            errorResponse, 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
