package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Standardized service interface that defines common operations for all services.
 * This interface provides a consistent contract for basic CRUD operations with async support.
 *
 * @param <T> The entity type
 * @param <ID> The ID type of the entity
 */
public interface StandardService<T, ID> {
    
    /**
     * Find an entity by its ID.
     *
     * @param id The ID of the entity to find
     * @return A CompletableFuture containing an Optional with the entity if found, or empty if not found
     */
    CompletableFuture<Optional<T>> findById(ID id);
    
    /**
     * Find all entities.
     *
     * @return A CompletableFuture containing a list of all entities
     */
    CompletableFuture<List<T>> findAll();
    
    /**
     * Save a new entity.
     *
     * @param entity The entity to save
     * @return A CompletableFuture containing the saved entity
     */
    CompletableFuture<T> save(T entity);
    
    /**
     * Update an existing entity.
     *
     * @param id The ID of the entity to update
     * @param entity The updated entity data
     * @return A CompletableFuture containing the updated entity
     */
    CompletableFuture<T> update(ID id, T entity);
    
    /**
     * Delete an entity by its ID.
     *
     * @param id The ID of the entity to delete
     * @return A CompletableFuture containing true if deleted, false otherwise
     */
    CompletableFuture<Boolean> deleteById(ID id);
    
    /**
     * Check if an entity exists by ID.
     *
     * @param id The ID to check
     * @return A CompletableFuture containing true if exists, false otherwise
     */
    CompletableFuture<Boolean> existsById(ID id);
    
    /**
     * Count all entities.
     *
     * @return A CompletableFuture containing the count of all entities
     */
    CompletableFuture<Long> count();
}