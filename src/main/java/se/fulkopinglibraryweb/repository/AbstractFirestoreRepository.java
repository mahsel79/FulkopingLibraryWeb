package se.fulkopinglibraryweb.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
import se.fulkopinglibraryweb.utils.FirestoreConfig;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import se.fulkopinglibraryweb.service.search.EnhancedSearchCriteria;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
public abstract class AbstractFirestoreRepository<T, ID> implements FirestoreRepository<T, ID> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Firestore firestore;
    protected final String collectionName;
    private final Class<T> entityClass;
    
    // Cache configuration
    private final ConcurrentHashMap<ID, T> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MINUTES = 30;
    private final ConcurrentHashMap<ID, Long> cacheTimestamps = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    protected AbstractFirestoreRepository(String collectionName) {
        this.firestore = FirestoreConfig.getInstance();
        this.collectionName = collectionName;
        this.entityClass = (Class<T>) ((java.lang.reflect.ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected <R> R tryExecute(FirestoreOperation<R> operation) throws InterruptedException, ExecutionException {
        return operation.execute();
    }

    public abstract List<String> getSearchableFields();

    @FunctionalInterface
    protected interface FirestoreOperation<R> {
        R execute() throws InterruptedException, ExecutionException;
    }

    @Override
    public T save(T entity) {
        try {
            return tryExecute(() -> {
                Map<String, Object> data = convertToMap(entity);
                DocumentReference docRef = firestore.collection(collectionName).document();
                docRef.set(data).get();
                DocumentSnapshot snapshot = docRef.get().get();
                T savedEntity = convertToEntity(snapshot);
                
                // Update cache with new entity
                @SuppressWarnings("unchecked")
                ID id = (ID) snapshot.getId();
                cache.put(id, savedEntity);
                cacheTimestamps.put(id, System.currentTimeMillis());
                
                return savedEntity;
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving entity", e);
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        // Check cache first
        if (cache.containsKey(id)) {
            long lastAccessed = cacheTimestamps.getOrDefault(id, 0L);
            if (System.currentTimeMillis() - lastAccessed < TimeUnit.MINUTES.toMillis(CACHE_TTL_MINUTES)) {
                return Optional.of(cache.get(id));
            }
        }

        try {
            return tryExecute(() -> {
                DocumentSnapshot document = firestore.collection(collectionName)
                        .document(id.toString())
                        .get()
                        .get();
                
                if (document.exists()) {
                    T entity = convertToEntity(document);
                    // Update cache
                    cache.put(id, entity);
                    cacheTimestamps.put(id, System.currentTimeMillis());
                    return Optional.of(entity);
                }
                return Optional.empty();
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error finding entity by id", e);
            throw new RuntimeException("Failed to find entity by id", e);
        }
    }

    @Override
    public List<T> findAll() {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collectionName).get().get();
            List<T> entities = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                try {
                    T entity = convertToEntity(document);
                    entities.add(entity);
                    
                    // Update cache with found entity
                    @SuppressWarnings("unchecked")
                    ID id = (ID) document.getId();
                    cache.put(id, entity);
                    cacheTimestamps.put(id, System.currentTimeMillis());
                    logger.debug("Updated cache for entity with id: {} from findAll query", id);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error converting document to entity", e);
                    throw new RuntimeException("Failed to convert document to entity", e);
                }
            }
            return entities;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error finding all entities", e);
            throw new RuntimeException("Failed to find all entities", e);
        }
    }

    @Override
    public Boolean deleteById(ID id) {
        try {
            ApiFuture<WriteResult> result = firestore.collection(collectionName)
                    .document(id.toString())
                    .delete();
            result.get();
            
            // Remove from cache
            cache.remove(id);
            cacheTimestamps.remove(id);
            
            return true;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting entity", e);
            return false;
        }
    }

    @Override
    public List<T> findByField(String field, Object value) {
        try {
            return tryExecute(() -> {
                QuerySnapshot querySnapshot = firestore.collection(collectionName)
                        .whereEqualTo(field, value)
                        .get()
                        .get();
                List<T> entities = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    try {
                        T entity = convertToEntity(document);
                        entities.add(entity);
                        
                        // Update cache with found entity
                        @SuppressWarnings("unchecked")
                        ID id = (ID) document.getId();
                        cache.put(id, entity);
                        cacheTimestamps.put(id, System.currentTimeMillis());
                        logger.debug("Updated cache for entity with id: {} from findByField query", id);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Error converting document to entity", e);
                        throw new RuntimeException("Failed to convert document to entity", e);
                    }
                }
                return entities;
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error finding entities by field", e);
            throw new RuntimeException("Failed to find entities by field", e);
        }
    }

    @Override
    public List<T> findByFieldSync(String field, Object value) {
        return findByField(field, value);
    }

    public Optional<T> findByIdSync(ID id) {
        try {
            return tryExecute(() -> {
                DocumentSnapshot document = firestore.collection(collectionName)
                        .document(id.toString())
                        .get()
                        .get();
                if (document.exists()) {
                    T entity = convertToEntity(document);
                    // Update cache
                    cache.put(id, entity);
                    cacheTimestamps.put(id, System.currentTimeMillis());
                    logger.debug("Updated cache for entity with id: {} from findByIdSync query", id);
                    return Optional.of(entity);
                }
                return Optional.empty();
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error finding entity by id (sync)", e);
            throw new RuntimeException("Failed to find entity by id (sync)", e);
        }
    }

    @Override
    public T update(ID id, Map<String, Object> updates) {
        try {
            return tryExecute(() -> {
                DocumentReference docRef = firestore.collection(collectionName).document(id.toString());
                docRef.update(updates).get();
                DocumentSnapshot updated = docRef.get().get();
                T updatedEntity = convertToEntity(updated);
                
                // Force update cache with modified entity
                cache.put(id, updatedEntity);
                cacheTimestamps.put(id, System.currentTimeMillis());
                logger.debug("Updated cache for entity with id: {}", id);
                
                return updatedEntity;
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error updating entity", e);
            throw new RuntimeException("Failed to update entity", e);
        }
    }

    public Optional<T> getById(ID id) {
        return findByIdSync(id);
    }

    @Override
    public List<T> search(SearchCriteria criteria) {
        try {
            return tryExecute(() -> {
                Query query = firestore.collection(collectionName);
                
                if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
                    String defaultSearchField = getDefaultSearchField();
                    query = query.whereGreaterThanOrEqualTo(defaultSearchField, criteria.getSearchTerm())
                            .whereLessThanOrEqualTo(defaultSearchField, criteria.getSearchTerm() + "\uf8ff");
                }
                
                if (criteria.getFilterField() != null && criteria.getFilterValue() != null) {
                    query = query.whereEqualTo(criteria.getFilterField(), criteria.getFilterValue());
                }
                
                if (criteria.getSortField() != null) {
                    Query.Direction direction = criteria.getSortDirection() == SearchCriteria.SortDirection.DESC 
                            ? Query.Direction.DESCENDING 
                            : Query.Direction.ASCENDING;
                    query = query.orderBy(criteria.getSortField(), direction);
                }
                
                QuerySnapshot querySnapshot = query.get().get();
                List<T> entities = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    try {
                        T entity = convertToEntity(document);
                        entities.add(entity);
                        
                        // Update cache with found entity
                        @SuppressWarnings("unchecked")
                        ID id = (ID) document.getId();
                        cache.put(id, entity);
                        cacheTimestamps.put(id, System.currentTimeMillis());
                        logger.debug("Updated cache for entity with id: {} from search query", id);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Error converting document to entity", e);
                        throw new RuntimeException("Failed to convert document to entity", e);
                    }
                }
                return entities;
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error during search operation", e);
            throw new RuntimeException("Failed to execute search", e);
        }
    }

    @Override
    public CollectionReference getCollection() {
        return firestore.collection(collectionName);
    }

    @Override
    public Long count() {
        try {
            return tryExecute(() -> {
            QuerySnapshot querySnapshot = firestore.collection(collectionName).get().get();
            return (long) querySnapshot.size();
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error counting entities", e);
            throw new RuntimeException("Failed to count entities", e);
        }
    }

    @Override
    public Boolean deleteAll() {
        try {
            return tryExecute(() -> {
            QuerySnapshot querySnapshot = firestore.collection(collectionName).get().get();
            List<ApiFuture<WriteResult>> futures = querySnapshot.getDocuments().stream()
                    .map(document -> document.getReference().delete())
                    .collect(Collectors.toList());
            
            for (ApiFuture<WriteResult> future : futures) {
                future.get();
            }
            
            // Clear entire cache
            cache.clear();
            cacheTimestamps.clear();
            
            return true;
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting all entities", e);
            throw new RuntimeException("Failed to delete all entities", e);
        }
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        try {
            return tryExecute(() -> {
                WriteBatch batch = firestore.batch();
                List<DocumentReference> docRefs = new ArrayList<>();
                
                // First pass: Create all documents and add to batch
                for (T entity : entities) {
                    Map<String, Object> data = convertToMap(entity);
                    DocumentReference docRef = firestore.collection(collectionName).document();
                    batch.set(docRef, data);
                    docRefs.add(docRef);
                }
                
                // Execute batch write
                batch.commit().get();
                
                // Second pass: Retrieve all saved documents and update cache
                List<T> savedEntities = new ArrayList<>();
                for (DocumentReference docRef : docRefs) {
                    DocumentSnapshot snapshot = docRef.get().get();
                    T savedEntity = convertToEntity(snapshot);
                    savedEntities.add(savedEntity);
                    
                    // Update cache with new entity
                    @SuppressWarnings("unchecked")
                    ID id = (ID) snapshot.getId();
                    cache.put(id, savedEntity);
                    cacheTimestamps.put(id, System.currentTimeMillis());
                }
                
                return savedEntities;
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving all entities", e);
            throw new RuntimeException("Failed to save all entities", e);
        }
    }

    protected String getDefaultSearchField() {
        return "name";
    }

    protected Map<String, Object> convertToMap(T entity) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : entityClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value != null) {
                    map.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                logger.warn("Failed to access field {}", field.getName(), e);
            }
        }
        return map;
    }

    protected T convertToEntity(DocumentSnapshot document) throws InterruptedException, ExecutionException {
        if (document == null || !document.exists()) {
            return null;
        }

        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = document.get(field.getName());
                    if (value != null) {
                        field.set(entity, value);
                    }
                } catch (Exception e) {
                    logger.error("Error getting field {} from document", field.getName(), e);
                    throw e; // Re-throw the original exception
                }
            }
            return entity;
        } catch (Exception e) {
            logger.error("Failed to convert document to entity", e);
            throw new RuntimeException("Failed to convert document to entity", e);
        }
    }
}
