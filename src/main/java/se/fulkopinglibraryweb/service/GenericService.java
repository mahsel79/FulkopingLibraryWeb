package se.fulkopinglibraryweb.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Generic service interface that defines common operations for all services.
 * This interface provides a standardized contract for basic CRUD operations.
 *
 * @param <T> The entity type
 * @param <ID> The ID type of the entity
 */
public interface GenericService<T, ID> {
    
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
     * Save an entity.
     *
     * @param entity The entity to save
     * @return A CompletableFuture containing the saved entity
     */
    CompletableFuture<T> save(T entity);
    
    /**
     * Update an entity.
     *
     * @param id The ID of the entity to update
     * @param entity The updated entity
     * @return A CompletableFuture containing the updated entity
     */
    CompletableFuture<T> update(ID id, T entity);
    
    /**
     * Delete an entity by its ID.
     *
     * @param id The ID of the entity to delete
     * @return A CompletableFuture containing a boolean indicating if the entity was deleted
     */
    CompletableFuture<Boolean> deleteById(ID id);
    
    /**
     * Check if an entity exists by its ID.
     *
     * @param id The ID of the entity to check
     * @return A CompletableFuture containing a boolean indicating if the entity exists
     */
    CompletableFuture<Boolean> existsById(ID id);
    
    /**
     * Count the number of entities.
     *
     * @return A CompletableFuture containing the number of entities
     */
    CompletableFuture<Long> count();
}
