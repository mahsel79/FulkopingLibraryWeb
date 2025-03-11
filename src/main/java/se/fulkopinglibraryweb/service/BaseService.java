package se.fulkopinglibraryweb.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Base service interface that defines standard CRUD operations for all services.
 * @param <T> The type of entity this service manages
 * @param <ID> The type of the entity's identifier
 */
public interface BaseService<T, ID> {
    /**
     * Retrieve all entities of type T.
     *
     * @return List of all entities
     * @throws ExecutionException If there's an error executing the query
     * @throws InterruptedException If the operation is interrupted
     */
    List<T> getAll() throws ExecutionException, InterruptedException;

    /**
     * Retrieve an entity by its ID.
     *
     * @param id The ID of the entity to retrieve
     * @return Optional containing the entity if found, empty otherwise
     * @throws ExecutionException If there's an error executing the query
     * @throws InterruptedException If the operation is interrupted
     */
    Optional<T> getById(ID id) throws ExecutionException, InterruptedException;

    /**
     * Create a new entity.
     *
     * @param entity The entity to create
     * @return The created entity
     * @throws ExecutionException If there's an error executing the operation
     * @throws InterruptedException If the operation is interrupted
     */
    T create(T entity) throws ExecutionException, InterruptedException;

    /**
     * Update an existing entity.
     *
     * @param entity The entity to update
     * @return The updated entity
     * @throws ExecutionException If there's an error executing the operation
     * @throws InterruptedException If the operation is interrupted
     */
    T update(T entity) throws ExecutionException, InterruptedException;

    /**
     * Delete an entity by its ID.
     *
     * @param id The ID of the entity to delete
     * @throws ExecutionException If there's an error executing the operation
     * @throws InterruptedException If the operation is interrupted
     */
    void delete(ID id) throws ExecutionException, InterruptedException;

    /**
     * Search for entities based on search criteria.
     *
     * @param searchType The type of search to perform
     * @param searchQuery The search query
     * @return List of entities matching the search criteria
     * @throws ExecutionException If there's an error executing the query
     * @throws InterruptedException If the operation is interrupted
     */
    List<T> search(String searchType, String searchQuery) throws ExecutionException, InterruptedException;
}