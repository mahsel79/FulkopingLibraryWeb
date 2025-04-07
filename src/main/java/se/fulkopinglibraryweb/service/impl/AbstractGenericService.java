package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.repository.FirestoreRepository;
import se.fulkopinglibraryweb.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract implementation of the GenericService interface.
 * This class provides a base implementation for all service classes.
 *
 * @param <T> The entity type
 * @param <ID> The ID type of the entity
 * @param <R> The repository type
 */
public abstract class AbstractGenericService<T, ID, R extends FirestoreRepository<T, ID>> implements GenericService<T, ID> {
    
    protected final R repository;
    protected final Logger logger;
    
    /**
     * Constructor for AbstractGenericService.
     *
     * @param repository The repository to use
     */
    public AbstractGenericService(R repository) {
        this.repository = repository;
        this.logger = LoggerFactory.getLogger(getClass());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Optional<T>> findById(ID id) {
        logger.debug("Entering findById with id: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.findById(id);
            } catch (Exception e) {
                logger.error("Error finding entity by ID: {}", id, e);
                throw new RuntimeException("Error finding entity by ID: " + id, e);
            }
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<List<T>> findAll() {
        logger.debug("Entering findAll");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.findAll();
            } catch (Exception e) {
                logger.error("Error finding all entities", e);
                throw new RuntimeException("Error finding all entities", e);
            }
        });
    }
    
    @Override
    @Transactional
    public CompletableFuture<T> save(T entity) {
        logger.debug("Entering save with entity: {}", entity);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.save(entity);
            } catch (Exception e) {
                logger.error("Error saving entity: {}", entity, e);
                throw new RuntimeException("Error saving entity", e);
            }
        });
    }
    
    @Override
    @Transactional
    public CompletableFuture<T> update(ID id, T entity) {
        logger.debug("Entering update with id: {} and entity: {}", id, entity);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.save(entity);
            } catch (Exception e) {
                logger.error("Error updating entity with ID: {}", id, e);
                throw new RuntimeException("Error updating entity with ID: " + id, e);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Boolean> existsById(ID id) {
        logger.debug("Entering existsById with id: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.existsById(id);
            } catch (Exception e) {
                logger.error("Error checking existence of entity with ID: {}", id, e);
                throw new RuntimeException("Error checking existence of entity with ID: " + id, e);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Long> count() {
        logger.debug("Entering count");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.count();
            } catch (Exception e) {
                logger.error("Error counting entities", e);
                throw new RuntimeException("Error counting entities", e);
            }
        });
    }

    @Override
    @Transactional
    public CompletableFuture<Boolean> deleteById(ID id) {
        logger.debug("Entering deleteById with id: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                repository.deleteById(id);
                return true;
            } catch (Exception e) {
                logger.error("Error deleting entity with ID: {}", id, e);
                return false;
            }
        });
    }
}
