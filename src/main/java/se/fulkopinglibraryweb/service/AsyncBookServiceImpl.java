package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.monitoring.PerformanceMonitor;
import se.fulkopinglibraryweb.repository.BookRepository;
import se.fulkopinglibraryweb.service.interfaces.BookService;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncBookServiceImpl implements BookService, GenericService<Book, String> {
    private final BookRepository bookRepository;
    private final PerformanceMonitor monitor;

    public AsyncBookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.monitor = PerformanceMonitor.getInstance();
    }

    @Override
    public Map<String, Object> convertEntityToMap(Book entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entity.getId());
        map.put("title", entity.getTitle());
        map.put("author", entity.getAuthor());
        map.put("isbn", entity.getIsbn());
        map.put("year", entity.getYear());
        map.put("available", entity.isAvailable());
        return map;
    }

    private static final Logger logger = LoggerFactory.getLogger(AsyncBookServiceImpl.class);

    @Override
    public Book read(String id) {
        return findById(id);
    }

    @Override
    public boolean existsById(String id) {
        try {
            monitor.recordRequestStart("book_exists");
            return bookRepository.existsById(id);
        } catch (Exception e) {
            monitor.recordError("book_exists");
            logger.error("Exists check failed", e);
            return false;
        }
    }

    @Override
    public long count() {
        try {
            monitor.recordRequestStart("book_count");
            return bookRepository.count();
        } catch (Exception e) {
            monitor.recordError("book_count");
            logger.error("Count failed", e);
            return 0;
        }
    }

    @Override
    public Optional<Book> save(Book entity) {
        if (entity.getId() == null) {
            return create(entity);
        }
        return update(entity);
    }

    @Override
    public Optional<Book> create(Book entity) {
        try {
            monitor.recordRequestStart("book_create");
            Book savedBook = bookRepository.save(entity);
            monitor.recordRequestEnd("book_create");
            return Optional.of(savedBook);
        } catch (Exception e) {
            monitor.recordError("book_create");
            logger.error("Create failed", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Book> update(String id, Book entity) {
        if (!id.equals(entity.getId())) {
            throw new IllegalArgumentException("ID mismatch in update");
        }
        return update(entity);
    }

    @Override
    public boolean deleteById(String id) {
        try {
            delete(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Book getById(String id) {
        return findById(id);
    }

    @Override
    public Optional<Book> update(Book entity) {
        try {
            monitor.recordRequestStart("book_update");
            Book updatedBook = bookRepository.save(entity);
            return Optional.of(updatedBook);
        } catch (Exception e) {
            monitor.recordError("book_update");
            logger.error("Update failed", e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String id) {
        try {
            monitor.recordRequestStart("book_delete");
            bookRepository.deleteById(id);
            monitor.recordRequestEnd("book_delete");
        } catch (Exception e) {
            monitor.recordError("book_delete");
            logger.error("Delete failed", e);
            throw new RuntimeException("Delete failed", e);
        }
    }

    @Override
    public List<Book> findAll() {
        try {
            monitor.recordRequestStart("book_findAll");
            return bookRepository.findAll();
        } catch (Exception e) {
            monitor.recordError("book_findAll");
            logger.error("Find all failed", e);
            throw new RuntimeException("Find all failed", e);
        }
    }

    @Override
    public Book findById(String id) {
        try {
            monitor.recordRequestStart("book_read");
            return bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        } catch (Exception e) {
            monitor.recordError("book_read");
            logger.error("Find by id failed", e);
            throw new RuntimeException("Find by id failed", e);
        }
    }

    @Override
    public Book read(String id) {
        return findById(id);
    }

    @Override
    public Map<String, Object> convertEntityToMap(Book entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entity.getId());
        map.put("title", entity.getTitle());
        map.put("author", entity.getAuthor());
        map.put("isbn", entity.getIsbn());
        map.put("year", entity.getYear());
        map.put("available", entity.isAvailable());
        return map;
    }

    @Override
    public boolean isAvailable(String bookId) {
        try {
            return findById(bookId).isAvailable();
        } catch (Exception e) {
            logger.error("Availability check failed", e);
            return false;
        }
    }

    @Override
    public boolean reserveBook(String bookId) {
        try {
            Book book = findById(bookId);
            if (!book.isAvailable()) {
                return false;
            }
            book.setAvailable(false);
            update(book);
            return true;
        } catch (Exception e) {
            logger.error("Reservation failed", e);
            return false;
        }
    }

    @Override
    public boolean cancelReservation(String bookId) {
        try {
            Book book = findById(bookId);
            if (book.isAvailable()) {
                return false;
            }
            book.setAvailable(true);
            update(book);
            return true;
        } catch (Exception e) {
            logger.error("Cancel reservation failed", e);
            return false;
        }
    }

    @Override
    public List<Book> searchBooks(SearchCriteria criteria) {
        try {
            monitor.recordRequestStart("book_search");
            List<Book> results = bookRepository.searchBooks(criteria);
            monitor.recordRequestEnd("book_search");
            return results;
        } catch (Exception e) {
            monitor.recordError("book_search");
            logger.error("Search books failed", e);
            throw new RuntimeException("Search books failed", e);
        }
    }

    @Override
    public Book findByIsbn(String isbn) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setFilterField("isbn");
        criteria.setFilterValue(isbn);
        return searchBooks(criteria).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + isbn));
    }

    @Override
    public List<Book> findByAuthor(String author) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setFilterField("author");
        criteria.setFilterValue(author);
        return searchBooks(criteria);
    }

    @Override
    public List<Book> findByTitle(String title) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setFilterField("title");
        criteria.setFilterValue(title);
        return searchBooks(criteria);
    }

    @Override
    public List<Book> findByYear(int year) {
        try {
            return bookRepository.findByYear(year);
        } catch (Exception e) {
            logger.error("Failed to find books by year", e);
            throw new RuntimeException("Failed to find books by year", e);
        }
    }
}
