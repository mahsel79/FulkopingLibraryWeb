package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AsyncBookService {
    CompletableFuture<Book> create(Book entity);
    CompletableFuture<Optional<Book>> findById(String id);
    CompletableFuture<List<Book>> findAll();
    CompletableFuture<Book> update(Book entity);
    CompletableFuture<Void> delete(String id);
    CompletableFuture<List<Book>> searchBooks(SearchCriteria criteria);
    CompletableFuture<Optional<Book>> findByIsbn(String isbn);
    CompletableFuture<List<Book>> findByAuthor(String author);
    CompletableFuture<List<Book>> findByTitle(String title);
    CompletableFuture<List<Book>> findByGenre(String genre);
    CompletableFuture<List<Book>> findByPublisher(String publisher);
    CompletableFuture<List<Book>> findByYear(int year);
}
