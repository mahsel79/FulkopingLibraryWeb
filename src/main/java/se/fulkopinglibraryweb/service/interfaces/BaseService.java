package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;

/**
 * Base interface for all services in the library system.
 * Defines common functionality that all services should implement.
 */
public interface BaseService<T, ID> extends CrudOperations<T, ID> {
    /**
     * Initializes the service with any required resources.
     */
    void initialize();

    /**
     * Cleans up any resources used by the service.
     */
    void cleanup();

    /**
     * Checks if the service is currently operational.
     *
     * @return true if the service is operational, false otherwise
     */
    boolean isOperational();

    /**
     * Gets the name of the service implementation.
     *
     * @return the service name
     */
    String getServiceName();

    /**
     * Find entities by a specific field value.
     *
     * @param fieldName the name of the field to search by
     * @param value the value to search for
     * @return list of matching entities
     */
    List<T> findByField(String fieldName, Object value);

    /**
     * Find entities matching the given criteria.
     *
     * @param criteria the search criteria
     * @return list of matching entities
     */
    List<T> findByCriteria(T criteria);

    /**
     * Count total number of entities.
     *
     * @return total count of entities
     */
    long count();

    /**
     * Save multiple entities in batch.
     *
     * @param entities list of entities to save
     * @return list of saved entities
     */
    List<T> saveAll(List<T> entities);

    /**
     * Delete multiple entities by their IDs.
     *
     * @param ids list of entity IDs to delete
     */
    void deleteAllById(List<ID> ids);
}
