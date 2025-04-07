package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.exception.BookServiceException;
import se.fulkopinglibraryweb.repository.BookRepository;
import se.fulkopinglibraryweb.service.interfaces.AsyncBookService;
import se.fulkopinglibraryweb.service.interfaces.AsyncCrudOperations;
import se.fulkopinglibraryweb.service.interfaces.AsyncGenericService;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import se.fulkopinglibraryweb.mappers.BookMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class AsyncBookServiceImpl implements AsyncBookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    protected final Executor executorService;

    public AsyncBookServiceImpl(BookRepository bookRepository, BookMapper bookMapper, Executor executorService) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<Optional<Book>> read(String id) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findById(id), executorService);
    }

    @Override
    public CompletableFuture<Book> update(Book book) {
        return CompletableFuture.supplyAsync(() -> bookRepository.save(book), executorService);
    }

    @Override
    public CompletableFuture<Book> create(Book book) {
        return CompletableFuture.supplyAsync(() -> bookRepository.save(book), executorService);
    }

    @Override
    public CompletableFuture<Book> getById(String id) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found")), executorService);
    }

    @Override
    public CompletableFuture<List<Book>> getAll() {
        return CompletableFuture.supplyAsync(() -> bookRepository.findAll(), executorService);
    }

    @Override
    public CompletableFuture<Boolean> isAvailable(String bookId) {
        return CompletableFuture.supplyAsync(() -> {
            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
            return book.isAvailable();
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        return CompletableFuture.runAsync(() -> bookRepository.deleteById(id), executorService);
    }

    @Override
    public CompletableFuture<Boolean> reserveBook(String bookId) {
        return CompletableFuture.supplyAsync(() -> {
            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
            if (book.isAvailable()) {
                book.setAvailable(false);
                bookRepository.save(book);
                return true;
            }
            return false;
        }, executorService);
    }

    @Override
    public CompletableFuture<Boolean> cancelReservation(String bookId) {
        return CompletableFuture.supplyAsync(() -> {
            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
            if (!book.isAvailable()) {
                book.setAvailable(true);
                bookRepository.save(book);
                return true;
            }
            return false;
        }, executorService);
    }

    @Override
    public CompletableFuture<List<Book>> searchBooks(SearchCriteria criteria) {
        return CompletableFuture.supplyAsync(() -> {
            List<Book> results = bookRepository.search(criteria);
            return results != null ? results : List.of();
        }, executorService);
    }

    @Override
    public CompletableFuture<Book> findByIsbn(String isbn) {
        return CompletableFuture.supplyAsync(() -> {
            List<Book> books = bookRepository.findByIsbn(isbn);
            if (books.isEmpty()) {
                throw new BookServiceException(
                    "Book with ISBN " + isbn + " not found",
                    "FIND_BY_ISBN",
                    null,
                    BookServiceException.ErrorType.NOT_FOUND
                );
            }
            return books.get(0);
        }, executorService);
    }

    @Override
    public CompletableFuture<List<Book>> findByAuthor(String author) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findByAuthor(author), executorService);
    }

    @Override
    public CompletableFuture<List<Book>> findByTitle(String title) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findByTitle(title), executorService);
    }

    @Override
    public CompletableFuture<List<Book>> findByYear(int year) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findByYear(year), executorService);
    }
}
