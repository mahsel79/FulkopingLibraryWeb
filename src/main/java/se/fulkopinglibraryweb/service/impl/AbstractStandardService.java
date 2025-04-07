package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.service.interfaces.StandardService;
import se.fulkopinglibraryweb.repository.GenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base implementation of StandardService that provides common functionality
 * for all service implementations.
 *
 * @param <T> The entity type
 * @param <ID> The ID type of the entity
 */
public abstract class AbstractStandardService<T, ID> implements StandardService<T, ID> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractStandardService.class);
    
    protected final GenericRepository<T, ID> repository;
    
    protected AbstractStandardService(GenericRepository<T, ID> repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Optional<T>> findById(ID id) {
        logger.debug("Finding entity by ID: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.findById(id);
            } catch (Exception e) {
                logger.error("Error finding entity by ID: {}", id, e);
                throw new RuntimeException("Error finding entity", e);
            }
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<List<T>> findAll() {
        logger.debug("Finding all entities");
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
        logger.debug("Saving entity: {}", entity);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.save(entity);
            } catch (Exception e) {
                logger.error("Error saving entity: {}", entity, e);
                throw new RuntimeException("Error saving entity", e);
            }
        });
    }
    
    protected abstract Map<String, Object> convertEntityToMap(T entity);

    @Override
    @Transactional
    public CompletableFuture<T> update(ID id, T entity) {
        logger.debug("Updating entity with ID: {}", id);
        try {
            Map<String, Object> updateMap = convertEntityToMap(entity);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    repository.update(id, updateMap);
                    return entity;
                } catch (Exception e) {
                    logger.error("Error updating entity with ID: {}", id, e);
                    throw new RuntimeException("Error updating entity", e);
                }
            });
        } catch (Exception e) {
                logger.error("Error converting entity to map for ID: {}", id, e);
            throw new RuntimeException("Error converting entity to map", e);
        }
    }
    
    @Override
    @Transactional
    public CompletableFuture<Boolean> deleteById(ID id) {
        logger.debug("Deleting entity with ID: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.deleteById(id);
            } catch (Exception e) {
                logger.error("Error deleting entity with ID: {}", id, e);
                throw new RuntimeException("Error deleting entity", e);
            }
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Boolean> existsById(ID id) {
        logger.debug("Checking if entity exists with ID: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.existsById(id);
            } catch (Exception e) {
                logger.error("Error checking if entity exists with ID: {}", id, e);
                throw new RuntimeException("Error checking if entity exists", e);
            }
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Long> count() {
        logger.debug("Counting all entities");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.count();
            } catch (Exception e) {
                logger.error("Error counting entities", e);
                throw new RuntimeException("Error counting entities", e);
            }
        });
    }
}
