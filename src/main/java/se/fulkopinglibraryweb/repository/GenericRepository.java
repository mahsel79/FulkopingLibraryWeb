package se.fulkopinglibraryweb.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Generic repository interface for data access operations.
 * Provides a standard set of CRUD operations that can be implemented
 * for any data storage mechanism.
 *
 * @param <T> The entity type this repository manages
 * @param <ID> The type of the entity's ID field
 */
public interface GenericRepository<T, ID> {
    
    /**
     * Save an entity to the data store.
     *
     * @param entity The entity to save
     * @return The saved entity
     */
    T save(T entity);
    
    /**
     * Find an entity by its ID.
     *
     * @param id The ID of the entity to find
     * @return An Optional with the found entity, or empty if not found
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities in the collection.
     *
     * @return A list of all entities
     */
    List<T> findAll();
    
    /**
     * Delete an entity by its ID.
     *
     * @param id The ID of the entity to delete
     * @return true if deletion was successful
     */
    Boolean deleteById(ID id);
    
    /**
     * Update specific fields of an entity.
     *
     * @param id The ID of the entity to update
     * @param updates Map of field names to new values
     * @return The updated entity
     */
    T update(ID id, Map<String, Object> updates);
    
    /**
     * Check if an entity with the given ID exists.
     *
     * @param id The ID to check
     * @return true if an entity with the given ID exists
     */
    default Boolean existsById(ID id) {
        return findById(id).isPresent();
    }
    
    /**
     * Count the number of entities in the collection.
     *
     * @return The count of entities
     */
    Long count();
    
    /**
     * Delete all entities in the collection.
     *
     * @return true if deletion was successful
     */
    Boolean deleteAll();
    
    /**
     * Save multiple entities at once.
     *
     * @param entities The entities to save
     * @return The list of saved entities
     */
    List<T> saveAll(List<T> entities);
    
}
