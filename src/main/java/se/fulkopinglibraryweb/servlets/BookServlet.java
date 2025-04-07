package se.fulkopinglibraryweb.servlets;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.interfaces.BookService;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/books/*")
public class BookServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BookServlet.class);
    private BookService bookService;

    @Override
    public void init() throws ServletException {
        super.init();
        bookService = (BookService) getServletContext().getAttribute("bookService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            handleListBooks(request, response);
        } else if (pathInfo.matches("/\\d+")) {
            handleGetBook(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleListBooks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchType = request.getParameter("searchType");
        String searchQuery = request.getParameter("searchQuery");

        List<Book> bookList;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            logger.info("Searching books by {} = {}", searchType, searchQuery);
            SearchCriteria criteria = new SearchCriteria();
            criteria.setFilterField(searchType);
            criteria.setFilterValue(searchQuery);
            bookList = bookService.searchBooks(criteria);
        } else {
            bookList = bookService.findAll();
        }

        request.setAttribute("bookList", bookList);
        request.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(request, response);
    }

    private void handleGetBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String bookId = pathInfo.substring(1);
        
        Book book = bookService.findById(bookId);
        if (book == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }
        request.setAttribute("book", book);
        request.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            validateBookParameters(request);
            
            Book newBook = createBookFromRequest(request);
            bookService.save(newBook);

            logger.info("New book added: {}", newBook.getTitle());
            response.sendRedirect(request.getContextPath() + "/books");
            
        } catch (IllegalArgumentException e) {
            handleValidationError(request, response, e.getMessage());
        } catch (Exception e) {
            handleServerError(response, "Error adding book", e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID");
            return;
        }

        try {
            String bookId = pathInfo.substring(1);
            validateBookParameters(request);
            
            Book updatedBook = createBookFromRequest(request);
            updatedBook.setId(bookId);
            
            Optional<Book> updatedBookOpt = bookService.update(updatedBook);
            if (updatedBookOpt.isPresent()) {
                Book updatedBookResult = updatedBookOpt.get();
                logger.info("Book updated: {}", updatedBook.getTitle());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            }
        } catch (IllegalArgumentException e) {
            handleValidationError(request, response, e.getMessage());
        } catch (Exception e) {
            handleServerError(response, "Error updating book", e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID");
            return;
        }

        try {
            String bookId = pathInfo.substring(1);
            bookService.deleteById(bookId);
            logger.info("Book deleted: {}", bookId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            handleServerError(response, "Error deleting book", e);
        }
    }

    private void validateBookParameters(HttpServletRequest request) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        String yearStr = request.getParameter("year");
        String pageCountStr = request.getParameter("pageCount");

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author is required");
        }
        if (isbn == null || !isbn.matches("^\\d{10}|\\d{13}$")) {
            throw new IllegalArgumentException("Invalid ISBN format");
        }
        try {
            int year = Integer.parseInt(yearStr);
            if (year < 1000 || year > 9999) {
                throw new IllegalArgumentException("Invalid publication year");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid publication year format");
        }
        try {
            int pageCount = Integer.parseInt(pageCountStr);
            if (pageCount <= 0) {
                throw new IllegalArgumentException("Page count must be positive");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page count format");
        }
    }

    private Book createBookFromRequest(HttpServletRequest request) {
        String title = request.getParameter("title");
        boolean available = Boolean.parseBoolean(request.getParameter("available"));
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        int year = Integer.parseInt(request.getParameter("year"));

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setYear(year);
        book.setAvailable(available);
        return book;
    }

    private void handleValidationError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(request, response);
    }

    private void handleServerError(HttpServletResponse response, String message, Exception e) throws IOException {
            logger.error("{}: {}", message, e.getMessage(), e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }
}
