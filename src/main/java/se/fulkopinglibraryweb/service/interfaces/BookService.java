package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.Optional;

public interface BookService extends GenericService<Book, String> {
    // Basic CRUD operations inherited from GenericService
    // Book-specific operations
    
    // Book-specific operations
    boolean isAvailable(String bookId);
    boolean reserveBook(String bookId);
    boolean cancelReservation(String bookId);
    List<Book> searchBooks(SearchCriteria criteria);
    Book findByIsbn(String isbn);
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);
    List<Book> findByYear(int year);
}
