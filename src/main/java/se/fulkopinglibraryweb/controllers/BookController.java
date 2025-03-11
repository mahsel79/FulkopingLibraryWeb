package se.fulkopinglibraryweb.controllers;

import com.google.gson.Gson;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.BookService;
import se.fulkopinglibraryweb.service.LibraryService;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import org.slf4j.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controller class handling HTTP requests for book-related operations.
 * This servlet manages RESTful endpoints for book management, including listing all books,
 * retrieving individual books, and handling book-related operations.
 *
 * @see BookService
 * @see LibraryService 
 * @see Book
 */
@WebServlet("/books/*")
public class BookController extends HttpServlet {
    private static final Logger logger = LoggingUtils.getLogger(BookController.class);
    private final BookService bookService;
    private final LibraryService libraryService;
    private final Gson gson;

    /**
     * Initializes the BookController with required dependencies.
     * Sets up the BookService, LibraryService and Gson parser for JSON handling.
     */
    public BookController(BookService bookService, LibraryService libraryService) {
        this.bookService = bookService;
        this.libraryService = libraryService;
        this.gson = new Gson();
    }

    /**
     * Configures the HTTP response for JSON output.
     * Sets appropriate content type and character encoding.
     *
     * @param response The HTTP response to be configured
     */
    private void setupJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    /**
     * Handles GET requests for book resources.
     * Supports two operations:
     * 1. Listing all books when path is "/books/" or "/books"
     * 2. Retrieving a specific book when path is "/books/{bookId}"
     *
     * @param request The HTTP request object
     * @param response The HTTP response object
     * @throws IOException If an I/O error occurs while processing the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Entering doGet - path: {}", request.getPathInfo());
        long startTime = System.currentTimeMillis();
        
        try {
            setupJsonResponse(response);
            String pathInfo = request.getPathInfo();

            try (PrintWriter out = response.getWriter()) {
                if (pathInfo == null || pathInfo.equals("/")) {
                    logger.debug("Fetching all books");
                    List<Book> books = bookService.getAllBooks();
                    out.print(gson.toJson(books));
                    logger.debug("Returned {} books", books.size());
                } else {
                    String bookId = pathInfo.substring(1);
                    logger.debug("Fetching book with ID: {}", bookId);
                    Book book = bookService.getBookById(bookId);
                    if (book != null) {
                        out.print(gson.toJson(book));
                        logger.debug("Successfully returned book: {}", bookId);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.toJson("Book not found"));
                        logger.warn("Book not found: {}", bookId);
                    }
                }
                out.flush();
            }
        } finally {
            logger.info("Exiting doGet - execution time: {}ms", 
                System.currentTimeMillis() - startTime);
        }
    }

    /**
     * Handles POST requests for borrowing books.
     * Expects JSON payload with userId and bookId
     *
     * @param request The HTTP request object
     * @param response The HTTP response object
     * @throws IOException If an I/O error occurs while processing the request
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Entering doPost - borrowing book");
        long startTime = System.currentTimeMillis();
        
        try {
            setupJsonResponse(response);
        
        try (PrintWriter out = response.getWriter()) {
            // Parse request body
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            logger.debug("Borrow request body: {}", requestBody);
            
            BorrowRequest borrowRequest = gson.fromJson(requestBody, BorrowRequest.class);
            logger.info("Processing borrow request - User: {}, Book: {}", 
                borrowRequest.userId, borrowRequest.bookId);
            
            // Perform borrow operation
            libraryService.borrowBook(borrowRequest.userId, borrowRequest.bookId);
            
            out.print(gson.toJson("Book borrowed successfully"));
            out.flush();
            logger.info("Book borrowed successfully - User: {}, Book: {}", 
                borrowRequest.userId, borrowRequest.bookId);
        } catch (Exception e) {
            logger.error("Error processing borrow request", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(e.getMessage()));
        }
        } finally {
            logger.info("Exiting doPost - execution time: {}ms", 
                System.currentTimeMillis() - startTime);
        }
    }

    /**
     * Handles PUT requests for returning books.
     * Expects JSON payload with userId and bookId
     *
     * @param request The HTTP request object
     * @param response The HTTP response object
     * @throws IOException If an I/O error occurs while processing the request
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Entering doPut - returning book");
        long startTime = System.currentTimeMillis();
        
        try {
            setupJsonResponse(response);
        
        try (PrintWriter out = response.getWriter()) {
            // Parse request body
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            logger.debug("Return request body: {}", requestBody);
            
            BorrowRequest borrowRequest = gson.fromJson(requestBody, BorrowRequest.class);
            logger.info("Processing return request - User: {}, Book: {}", 
                borrowRequest.userId, borrowRequest.bookId);
            
            // Perform return operation
            libraryService.returnBook(borrowRequest.userId, borrowRequest.bookId);
            
            out.print(gson.toJson("Book returned successfully"));
            out.flush();
            logger.info("Book returned successfully - User: {}, Book: {}", 
                borrowRequest.userId, borrowRequest.bookId);
        } catch (Exception e) {
            logger.error("Error processing return request", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(e.getMessage()));
        }
        } finally {
            logger.info("Exiting doPut - execution time: {}ms", 
                System.currentTimeMillis() - startTime);
        }
    }

    /**
     * Inner class representing borrow/return request payload
     */
    private static class BorrowRequest {
        String userId;
        String bookId;
    }
}
