package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.repository.FirestoreRepository;
import se.fulkopinglibraryweb.service.GenericService;
import se.fulkopinglibraryweb.utils.LoggingUtils;

import java.util.Map;
import java.util.HashMap;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    protected final LoggingUtils logger;
    
    /**
     * Constructor for AbstractGenericService.
     *
     * @param repository The repository to use
     */
    public AbstractGenericService(R repository) {
        this.repository = repository;
        this.logger = new LoggingUtils(getClass());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        LoggingUtils.logMethodEntry(logger.getLogger(), "findById", id);
        try {
            return repository.findById(id);
        } catch (Exception e) {
            LoggingUtils.logError(logger.getLogger(), "Error finding entity by ID: {}", id, e);
            throw new RuntimeException("Error finding entity by ID: " + id, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        LoggingUtils.logMethodEntry(logger, "findAll");
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.error("Error finding all entities", e);
            throw new RuntimeException("Error finding all entities", e);
        }
    }
    
    @Override
    @Transactional
    public Optional<T> save(T entity) {
        LoggingUtils.logMethodEntry(logger, "save", entity);
        try {
            return Optional.ofNullable(repository.save(entity));
        } catch (Exception e) {
            logger.error("Error saving entity: {}", entity, e);
            throw new RuntimeException("Error saving entity", e);
        }
    }
    
    @Override
    @Transactional
    public Optional<T> update(T entity) {
        LoggingUtils.logMethodEntry(logger, "update", entity);
        try {
            return Optional.ofNullable(repository.save(entity));
        } catch (Exception e) {
            logger.error("Error updating entity: {}", entity, e);
            throw new RuntimeException("Error updating entity", e);
        }
    }
    
    /**
     * Convert an entity to a Map for Firestore operations.
     * This method should be implemented by subclasses to provide entity-specific conversion.
     *
     * @param entity The entity to convert
     * @return A Map representation of the entity
     */
    protected abstract Map<String, Object> convertEntityToMap(T entity);
    
    @Override
    @Transactional(readOnly = true)
    public Optional<T> getById(ID id) {
        return findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> read(ID id) {
        return findById(id);
    }

    @Override
    @Transactional
    public Optional<T> update(ID id, T entity) {
        LoggingUtils.logMethodEntry(logger, "update", id, entity);
        try {
            return Optional.ofNullable(repository.save(entity));
        } catch (Exception e) {
            logger.error("Error updating entity with ID: {}", id, e);
            throw new RuntimeException("Error updating entity with ID: " + id, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        LoggingUtils.logMethodEntry(logger, "existsById", id);
        try {
            return repository.existsById(id);
        } catch (Exception e) {
            logger.error("Error checking existence of entity with ID: {}", id, e);
            throw new RuntimeException("Error checking existence of entity with ID: " + id, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        LoggingUtils.logMethodEntry(logger, "count");
        try {
            return repository.count();
        } catch (Exception e) {
            logger.error("Error counting entities", e);
            throw new RuntimeException("Error counting entities", e);
        }
    }

    @Override
    @Transactional
    public boolean deleteById(ID id) {
        LoggingUtils.logMethodEntry(logger, "deleteById", id);
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting entity with ID: {}", id, e);
            return false;
        }
    }
}
