package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;

public interface SyncBookService {
    boolean isAvailable(String bookId);
    boolean reserveBook(String bookId);
    boolean cancelReservation(String bookId);
    List<Book> searchBooks(SearchCriteria criteria);
    Book findByIsbn(String isbn);
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);
    List<Book> findByYear(int year);
}
