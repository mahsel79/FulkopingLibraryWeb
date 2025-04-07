package se.fulkopinglibraryweb.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fulkopinglibraryweb.utils.LoggerUtil;
import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.model.LibraryItem;
import se.fulkopinglibraryweb.service.interfaces.LoanService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Abstract base class for library services that provides common CRUD and loan operations.
 * @param <T> The type of library item (Book, Magazine, Media etc.)
 * @param <ID> The type of the item's identifier
 */
public abstract class AbstractLibraryService<T extends LibraryItem, ID> {
    protected final Firestore firestore;
    protected final LoanService loanService;
    protected final CollectionReference collection;
    protected final Class<T> entityClass;
    protected final String entityName;
    protected final Logger logger;

    protected AbstractLibraryService(Firestore firestore, LoanService loanService, CollectionReference collection, Class<T> entityClass, String entityName) {
        this.firestore = firestore;
        this.loanService = loanService;
        this.collection = collection;
        this.entityClass = entityClass;
        this.entityName = entityName;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Retrieves a library item by its unique identifier.
     *
     * @param id The unique identifier of the item
     * @return Optional containing the item if found, empty otherwise
     * @throws ExecutionException If there is an error executing the query
     * @throws InterruptedException If the operation is interrupted
     */
    public Optional<T> getById(ID id) throws ExecutionException, InterruptedException {
        try {
            var documentSnapshot = collection.document(id.toString()).get().get();
            return Optional.ofNullable(documentSnapshot.exists() ? documentSnapshot.toObject(entityClass) : null);
        } catch (InterruptedException e) {
            LoggerUtil.logError(this.getClass(), String.format("Thread interrupted while fetching %s: %s", entityName, id), e);
            Thread.currentThread().interrupt();
            throw e;
        } catch (ExecutionException e) {
            LoggerUtil.logError(this.getClass(), String.format("Error executing %s fetch: %s", entityName, id), e);
            throw e;
        }
    }

    /**
     * Retrieves all items of this type from the library.
     *
     * @return List of all items
     * @throws ExecutionException If there is an error executing the query
     * @throws InterruptedException If the operation is interrupted
     */
    public List<T> getAll() throws ExecutionException, InterruptedException {
        try {
            return collection.get().get().toObjects(entityClass);
        } catch (InterruptedException e) {
            LoggerUtil.logError(this.getClass(), String.format("Thread interrupted while fetching %s collection", entityName), e);
            Thread.currentThread().interrupt();
            throw e;
        } catch (ExecutionException e) {
            LoggerUtil.logError(this.getClass(), String.format("Error executing %s collection fetch", entityName), e);
            throw e;
        }
    }

    /**
     * Creates a new library item.
     *
     * @param item The item to create
     * @return The created item with updated information
     * @throws ExecutionException If there is an error executing the operation
     * @throws InterruptedException If the operation is interrupted
     */
    public T create(T item) throws ExecutionException, InterruptedException {
        try {
            if (item == null) {
                throw new IllegalArgumentException(entityName + " cannot be null");
            }
            DocumentReference docRef = collection.document();
            docRef.set(item).get();
            LoggerUtil.logInfo(this.getClass(), String.format("%s created successfully", entityName));
            return item;
        } catch (InterruptedException e) {
            LoggerUtil.logError(this.getClass(), String.format("Thread interrupted while creating %s", entityName), e);
            Thread.currentThread().interrupt();
            throw e;
        } catch (ExecutionException e) {
            LoggerUtil.logError(this.getClass(), String.format("Error executing %s creation", entityName), e);
            throw e;
        }
    }

    /**
     * Processes a borrow request for a library item.
     *
     * @param id The unique identifier of the item to borrow
     * @param userId The unique identifier of the user borrowing the item
     * @return true if the borrow operation was successful, false otherwise
     * @throws ExecutionException If there is an error executing the operation
     * @throws InterruptedException If the operation is interrupted
     */
    public boolean borrow(ID id, String userId) throws ExecutionException, InterruptedException {
        try {
            DocumentReference docRef = collection.document(id.toString());
            var item = docRef.get().get().toObject(entityClass);
            
            if (item != null) {
                docRef.update("available", false).get();
                loanService.borrowItem(userId, id.toString());
                LoggerUtil.logInfo(this.getClass(), String.format("%s borrowed successfully: %s", entityName, id));
                return true;
            }
            return false;
        } catch (Exception e) {
            LoggerUtil.logError(this.getClass(), String.format("Error borrowing %s: %s", entityName, id), e);
            return false;
        }
    }

    /**
     * Processes a return request for a borrowed library item.
     *
     * @param id The unique identifier of the item to return
     * @param userId The unique identifier of the user returning the item
     * @return true if the return operation was successful, false otherwise
     * @throws ExecutionException If there is an error executing the operation
     * @throws InterruptedException If the operation is interrupted
     */
    public boolean returnItem(ID id, String userId) throws ExecutionException, InterruptedException {
        try {
            DocumentReference docRef = collection.document(id.toString());
            var item = docRef.get().get().toObject(entityClass);
            
            if (item != null) {
                docRef.update("available", true).get();
                List<Loan> borrowedItems = loanService.getBorrowedItems(userId);
                for (Loan loan : borrowedItems) {
                    if (loan.getItemId().equals(id.toString())) {
                        loanService.returnItem(userId, id.toString());
                        LoggerUtil.logInfo(this.getClass(), String.format("%s returned successfully: %s", entityName, id));
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            LoggerUtil.logError(this.getClass(), String.format("Error returning %s: %s", entityName, id), e);
            return false;
        }
    }

    protected abstract Query buildSearchQuery(String searchType, String searchQuery);
}
