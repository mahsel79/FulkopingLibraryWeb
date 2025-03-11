package se.fulkopinglibraryweb.repository.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.FirestoreException;
import java.util.concurrent.ExecutionException;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.repository.BookRepository;
import se.fulkopinglibraryweb.repository.AbstractFirestoreRepository;
import se.fulkopinglibraryweb.repository.FirestoreRepository;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookFirestoreRepository extends AbstractFirestoreRepository<Book, String> implements BookRepository {
    private final FirestoreRepository<Book, String> firestoreRepository;

    public BookFirestoreRepository(FirestoreRepository<Book, String> firestoreRepository) {
        super("books");
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public List<Book> findAll() {
        try {
            QuerySnapshot querySnapshot = getCollection().get().get();
            return querySnapshot.getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to get all books", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while getting all books", e);
        }
    }

    @Override
    public void deleteAll(List<String> ids) {
        for (String id : ids) {
            try {
                getCollection().document(id).delete().get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed to delete book with id: " + id, e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while deleting book with id: " + id, e);
            }
        }
    }

    @Override
    public List<Book> findReservedBooks(String userId) {
        try {
            QuerySnapshot querySnapshot = getCollection()
                .whereEqualTo("reserved", true)
                .whereEqualTo("reservedBy", userId)
                .get()
                .get();
            return querySnapshot.getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find reserved books", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding reserved books", e);
        }
    }

    @Override
    public List<Book> searchBooks(SearchCriteria criteria) {
        try {
            Query query = getCollection();
            
            if (criteria.getFilterField() != null && criteria.getFilterValue() != null) {
                query = query.whereEqualTo(criteria.getFilterField(), criteria.getFilterValue());
            }
            
            if (criteria.getSortField() != null && criteria.getSortDirection() != null) {
                query = criteria.getSortDirection() == SearchCriteria.SortDirection.ASC ?
                    query.orderBy(criteria.getSortField(), Query.Direction.ASCENDING) :
                    query.orderBy(criteria.getSortField(), Query.Direction.DESCENDING);
            }
            
            QuerySnapshot querySnapshot = query.get().get();
            return querySnapshot.getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to search books", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while searching books", e);
        }
    }

    @Override
    public List<Book> findByField(String field, Object value) {
        try {
            QuerySnapshot querySnapshot = getCollection()
                .whereEqualTo(field, value)
                .get()
                .get();
            return querySnapshot.getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find books by " + field, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding books by " + field, e);
        }
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        return findByField("isbn", isbn);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return findByField("author", author);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return findByField("title", title);
    }

    @Override
    public List<Book> findByYear(int year) {
        return findByField("year", year);
    }

    @Override
    public List<String> getSearchableFields() {
        return List.of("title", "author", "isbn", "year");
    }

    @Override
    public Optional<Book> findById(String id) {
        try {
            DocumentSnapshot document = getCollection()
                .document(id)
                .get()
                .get();
            return Optional.ofNullable(convertToEntity(document));
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find book by id", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding book by id", e);
        }
    }

    @Override
    public Optional<Book> getById(String id) {
        return findById(id);
    }

    @Override
    public Long count() {
        try {
            AggregateQuerySnapshot countSnapshot = getCollection().count().get().get();
            return countSnapshot.getCount();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to count books", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while counting books", e);
        }
    }

    @Override
    public List<Book> saveAll(List<Book> entities) {
        for (Book book : entities) {
            try {
                getCollection().document(book.getId()).set(convertToMap(book)).get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed to save book with id: " + book.getId(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while saving book with id: " + book.getId(), e);
            }
        }
        return entities;
    }

    @Override
    public Boolean isAvailable(String bookId) {
        return getById(bookId)
            .map(Book::isAvailable)
            .orElse(false);
    }

    @Override
    public void reserve(String bookId) {
        try {
            getCollection().document(bookId).update("reserved", true).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to reserve book with id: " + bookId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while reserving book with id: " + bookId, e);
        }
    }

    @Override
    public void cancelReservation(String bookId) {
        try {
            getCollection().document(bookId).update("reserved", false).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to cancel reservation for book with id: " + bookId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while canceling reservation for book with id: " + bookId, e);
        }
    }

    @Override
    public Map<String, Object> convertToMap(Book entity) {
        return Map.of(
            "id", entity.getId(),
            "title", entity.getTitle(),
            "isbn", entity.getIsbn(),
            "author", entity.getAuthor(),
            "year", entity.getYear(),
            "available", entity.isAvailable(),
            "reserved", entity.isReserved()
        );
    }

    @Override
    public Book convertToEntity(DocumentSnapshot document) {
        Book book = new Book();
        book.setId(document.getString("id"));
        book.setTitle(document.getString("title"));
        book.setIsbn(document.getString("isbn"));
        book.setAuthor(document.getString("author"));
        book.setYear(Optional.ofNullable(document.getLong("year")).orElse(0L).intValue());
        book.setAvailable(Boolean.TRUE.equals(document.getBoolean("available")));
        book.setReserved(Boolean.TRUE.equals(document.getBoolean("reserved")));
        
        return book;
    }
}
