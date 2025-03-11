package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.interfaces.BaseService;
import java.util.List;

public interface BookService extends BaseService<Book, String> {
    Book getBookById(String id);
    Book findById(String id);
    List<Book> getAllBooks();
    List<Book> searchBooks(String query);
    List<Book> getAvailableBooks();
}
