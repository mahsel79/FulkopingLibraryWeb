package se.fulkopinglibraryweb.repository;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends FirestoreRepository<Book, String> {
    List<Book> findByIsbn(String isbn);
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);
    List<Book> findByYear(int year);
    List<Book> findAll();
    Boolean isAvailable(String bookId);
    void reserve(String bookId);
    void cancelReservation(String bookId);
    List<Book> findReservedBooks(String userId);
    List<Book> searchBooks(SearchCriteria criteria);
    List<String> getSearchableFields();
    Optional<Book> findById(String id);
    Optional<Book> getById(String id);
    Long count();
    List<Book> saveAll(List<Book> entities);
    void deleteAll(List<String> ids);
}
