package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncBookService extends AsyncCrudOperations<Book, String> {
    CompletableFuture<Boolean> isAvailable(String bookId);
    CompletableFuture<Boolean> reserveBook(String bookId);
    CompletableFuture<Boolean> cancelReservation(String bookId);
    CompletableFuture<List<Book>> searchBooks(SearchCriteria criteria);
    CompletableFuture<Book> findByIsbn(String isbn);
    CompletableFuture<List<Book>> findByAuthor(String author);
    CompletableFuture<List<Book>> findByTitle(String title);
    CompletableFuture<List<Book>> findByYear(int year);
}
