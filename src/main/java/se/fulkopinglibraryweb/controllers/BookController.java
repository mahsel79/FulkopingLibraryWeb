package se.fulkopinglibraryweb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.BookService;
import se.fulkopinglibraryweb.service.LibraryService;
import se.fulkopinglibraryweb.exception.ControllerException;
import se.fulkopinglibraryweb.dtos.BorrowRequest;

import java.util.List;

/**
 * REST controller for book-related operations.
 * Handles requests for book management including listing, borrowing and returning books.
 */
@RestController
@RequestMapping("/books")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;
    private final LibraryService libraryService;

    public BookController(BookService bookService, LibraryService libraryService) {
        this.bookService = bookService;
        this.libraryService = libraryService;
    }

    /**
     * Get all books
     * @return List of all books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        long startTime = System.currentTimeMillis();
        logger.debug("GET request received for all books");
        
        try {
            List<Book> books = bookService.getAllBooks();
            logger.info("Retrieved {} books in {} ms", 
                books.size(), System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error getting all books (took {} ms): {}", 
                System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw new ControllerException(
                500,
                "BOOK_RETRIEVAL_ERROR",
                "Failed to retrieve books",
                e.getMessage()
            );
        }
    }

    /**
     * Get a specific book by ID
     * @param bookId The ID of the book to retrieve
     * @return The requested book
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable String bookId) {
        long startTime = System.currentTimeMillis();
        logger.debug("GET request received for book ID: {}", bookId);
        
        try {
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                logger.warn("Book not found with ID: {} (took {} ms)", 
                    bookId, System.currentTimeMillis() - startTime);
                throw new ControllerException(
                    404,
                    "BOOK_NOT_FOUND",
                    "Book not found",
                    "Book with ID " + bookId + " does not exist"
                );
            }
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            logger.error("Error getting book with ID {} (took {} ms): {}", 
                bookId, System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw new ControllerException(
                500,
                "BOOK_RETRIEVAL_ERROR",
                "Failed to retrieve book",
                e.getMessage()
            );
        }
    }

    /**
     * Borrow a book
     * @param borrowRequest Contains userId and bookId
     * @return Success message
     */
    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody BorrowRequest borrowRequest) {
        long startTime = System.currentTimeMillis();
        logger.debug("POST request received to borrow book: {}", borrowRequest);
        
        try {
            libraryService.borrowBook(borrowRequest.getUserId(), borrowRequest.getBookId());
            logger.info("Book {} borrowed by user {} (took {} ms)", 
                borrowRequest.getBookId(), borrowRequest.getUserId(),
                System.currentTimeMillis() - startTime);
            return ResponseEntity.ok("Book borrowed successfully");
        } catch (Exception e) {
            logger.error("Error borrowing book (took {} ms): {}", 
                System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw new ControllerException(
                400,
                "BORROW_ERROR",
                "Failed to borrow book",
                e.getMessage()
            );
        }
    }

    /**
     * Return a book
     * @param borrowRequest Contains userId and bookId
     * @return Success message
     */
    @PutMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody BorrowRequest borrowRequest) {
        long startTime = System.currentTimeMillis();
        logger.debug("PUT request received to return book: {}", borrowRequest);
        
        try {
            libraryService.returnBook(borrowRequest.getUserId(), borrowRequest.getBookId());
            logger.info("Book {} returned by user {} (took {} ms)", 
                borrowRequest.getBookId(), borrowRequest.getUserId(),
                System.currentTimeMillis() - startTime);
            return ResponseEntity.ok("Book returned successfully");
        } catch (Exception e) {
            logger.error("Error returning book (took {} ms): {}", 
                System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw new ControllerException(
                400,
                "RETURN_ERROR",
                "Failed to return book",
                e.getMessage()
            );
        }
    }
}
