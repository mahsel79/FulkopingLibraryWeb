package se.fulkopinglibraryweb.service.impl;

import java.util.concurrent.CompletableFuture;
import se.fulkopinglibraryweb.config.UnifiedAppConfig;
import se.fulkopinglibraryweb.dtos.BookDTO;
import se.fulkopinglibraryweb.exception.BookServiceException;
import se.fulkopinglibraryweb.mappers.BookMapper;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.repository.BookRepository;
import se.fulkopinglibraryweb.service.AsyncBookServiceImpl;
import se.fulkopinglibraryweb.service.interfaces.BookService;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UnifiedAppConfig unifiedAppConfig;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper, UnifiedAppConfig unifiedAppConfig) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.unifiedAppConfig = unifiedAppConfig;
    }

    @Override
    public Optional<Book> save(Book entity) {
        try {
            Book savedBook = bookRepository.save(entity);
            return Optional.ofNullable(savedBook);
        } catch (Exception e) {
            throw new BookServiceException("Failed to save book", "save", entity.getId(),
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Book findById(String id) {
        try {
            Optional<Book> book = bookRepository.findById(id);
            if (book.isPresent()) {
                return book.get();
            }
            throw new BookServiceException("Book not found", "findById", id,
                BookServiceException.ErrorType.NOT_FOUND, null);
        } catch (Exception e) {
            throw new BookServiceException("Failed to find book", "findById", id,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Book getById(String id) {
        return findById(id);
    }

    @Override
    public Book read(String id) {
        return getById(id);
    }

    @Override
    public boolean deleteById(String id) {
        try {
            bookRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new BookServiceException("Failed to delete book", "deleteById", id,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Optional<Book> update(Book entity) {
        try {
            Book updatedBook = bookRepository.save(entity);
            return Optional.ofNullable(updatedBook);
        } catch (Exception e) {
            throw new BookServiceException("Failed to update book", "update", entity.getId(),
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Optional<Book> update(String id, Book entity) {
        return update(entity);
    }

    @Override
    public List<Book> findAll() {
        try {
            return bookRepository.findAll();
        } catch (Exception e) {
            throw new BookServiceException("Failed to find all books", "findAll", null,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            return bookRepository.existsById(id);
        } catch (Exception e) {
            throw new BookServiceException("Failed to check book existence", "existsById", id,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public long count() {
        try {
            return bookRepository.count();
        } catch (Exception e) {
            throw new BookServiceException("Failed to count books", "count", null,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Map<String, Object> convertEntityToMap(Book entity) {
        try {
            BookDTO dto = bookMapper.toDTO(entity);
            return Map.of(
                "id", dto.getId(),
                "title", dto.getTitle(),
                "author", dto.getAuthor(),
                "isbn", dto.getIsbn(),
                "year", dto.getYear(),
                "available", dto.isAvailable()
            );
        } catch (Exception e) {
            throw new BookServiceException("Failed to convert book to map", "convert", entity.getId(),
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public boolean isAvailable(String bookId) {
        try {
            return bookRepository.isAvailable(bookId);
        } catch (Exception e) {
            throw new BookServiceException("Availability check failed", "isAvailable", bookId,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public boolean reserveBook(String bookId) {
        try {
            bookRepository.reserve(bookId);
            return true;
        } catch (Exception e) {
            throw new BookServiceException("Reservation failed", "reserveBook", bookId,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public boolean cancelReservation(String bookId) {
        try {
            bookRepository.cancelReservation(bookId);
            return true;
        } catch (Exception e) {
            throw new BookServiceException("Cancel reservation failed", "cancelReservation", bookId,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Book> searchBooks(SearchCriteria criteria) {
        try {
            return bookRepository.searchBooks(criteria);
        } catch (Exception e) {
            throw new BookServiceException("Search failed", "searchBooks", null,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Book findByIsbn(String isbn) {
        try {
            List<Book> books = bookRepository.findByIsbn(isbn);
            if (books.isEmpty()) {
                throw new BookServiceException("Book not found", "findByIsbn", isbn,
                    BookServiceException.ErrorType.NOT_FOUND, null);
            }
            return books.get(0);
        } catch (Exception e) {
            throw new BookServiceException("Find by ISBN failed", "findByIsbn", isbn,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Book> syncSearchBooks(SearchCriteria criteria) {
        try {
            return bookRepository.searchBooks(criteria);
        } catch (Exception e) {
            throw new BookServiceException("Search failed", "syncSearchBooks", null,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        try {
            return bookRepository.findByTitle(title);
        } catch (Exception e) {
            throw new BookServiceException("Find by title failed", "findByTitle", title,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Book> findByAuthor(String author) {
        try {
            return bookRepository.findByAuthor(author);
        } catch (Exception e) {
            throw new BookServiceException("Find by author failed", "findByAuthor", author,
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Book syncFindByIsbn(String isbn) {
        return findByIsbn(isbn);
    }

    @Override
    public List<Book> syncFindByYear(int year) {
        try {
            return bookRepository.findByYear(year);
        } catch (Exception e) {
            throw new BookServiceException("Find by year failed", "syncFindByYear", String.valueOf(year),
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Book> syncFindByTitle(String title) {
        return findByTitle(title);
    }

    @Override
    public List<Book> syncFindByAuthor(String author) {
        return findByAuthor(author);
    }

    @Override
    public List<Book> findByYear(int year) {
        try {
            return bookRepository.findByYear(year);
        } catch (Exception e) {
            throw new BookServiceException("Find by year failed", "findByYear", String.valueOf(year),
                BookServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public boolean syncReserveBook(String bookId) {
        return reserveBook(bookId);
    }


    @Override
    public boolean syncCancelReservation(String bookId) {
        return cancelReservation(bookId);
    }

    @Override
    public boolean syncIsAvailable(String bookId) {
        return isAvailable(bookId);
    }
}
