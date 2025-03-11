package se.fulkopinglibraryweb.repository;

import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
import se.fulkopinglibraryweb.utils.FirestoreConfig;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import se.fulkopinglibraryweb.service.search.EnhancedSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
public abstract class AbstractFirestoreRepository<T, ID> implements FirestoreRepository<T, ID> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Firestore firestore;
    protected final String collectionName;

    protected AbstractFirestoreRepository(String collectionName) {
        this.firestore = FirestoreConfig.getInstance();
        this.collectionName = collectionName;
    }

    @Override
    public T save(T entity) {
        Map<String, Object> data = convertToMap(entity);
        try {
            DocumentReference docRef = firestore.collection(collectionName).document();
            ApiFuture<WriteResult> result = docRef.set(data);
            result.get();
            DocumentSnapshot snapshot = docRef.get().get();
            return convertToEntity(snapshot);
        } catch (Exception e) {
            logger.error("Error saving entity to Firestore: {}", e.getMessage());
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            DocumentSnapshot document = firestore.collection(collectionName)
                    .document(id.toString())
                    .get()
                    .get();
            return document.exists() ? Optional.of(convertToEntity(document)) : Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding entity by ID: {}", e.getMessage());
            throw new RuntimeException("Failed to find entity", e);
        }
    }

    @Override
    public List<T> findAll() {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collectionName).get().get();
            List<T> entities = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                entities.add(convertToEntity(document));
            }
            return entities;
        } catch (Exception e) {
            logger.error("Error finding all entities: {}", e.getMessage());
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
            return true;
        } catch (Exception e) {
            logger.error("Error deleting entity: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<T> findByField(String field, Object value) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collectionName)
                    .whereEqualTo(field, value)
                    .get()
                    .get();
            List<T> entities = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                entities.add(convertToEntity(document));
            }
            return entities;
        } catch (Exception e) {
            logger.error("Error finding entities by field: {}", e.getMessage());
            throw new RuntimeException("Failed to find entities by field", e);
        }
    }

    @Override
    public List<T> findByFieldSync(String field, String value) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collectionName)
                    .whereEqualTo(field, value)
                    .get()
                    .get();
            List<T> entities = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                entities.add(convertToEntity(document));
            }
            return entities;
        } catch (Exception e) {
            logger.error("Error finding entities by field: {}", e.getMessage());
            throw new RuntimeException("Failed to find entities by field", e);
        }
    }

    @Override
    public List<T> findByFieldSync(String field, int value) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collectionName)
                    .whereEqualTo(field, value)
                    .get()
                    .get();
            List<T> entities = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                entities.add(convertToEntity(document));
            }
            return entities;
        } catch (Exception e) {
            logger.error("Error finding entities by field: {}", e.getMessage());
            throw new RuntimeException("Failed to find entities by field", e);
        }
    }

    @Override
    public Optional<T> findByIdSync(String id) {
        try {
            DocumentSnapshot document = firestore.collection(collectionName)
                    .document(id)
                    .get()
                    .get();
            return document.exists() ? Optional.of(convertToEntity(document)) : Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding entity by ID: {}", e.getMessage());
            throw new RuntimeException("Failed to find entity", e);
        }
    }

    @Override
    public T update(ID id, Map<String, Object> updates) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(id.toString());
            ApiFuture<WriteResult> result = docRef.update(updates);
            result.get();
            DocumentSnapshot updated = docRef.get().get();
            return convertToEntity(updated);
        } catch (Exception e) {
            logger.error("Error updating entity: {}", e.getMessage());
            throw new RuntimeException("Failed to update entity", e);
        }
    }

    @Override
    public Optional<T> getById(ID id) {
        try {
            DocumentSnapshot document = firestore.collection(collectionName)
                    .document(id.toString())
                    .get()
                    .get();
            return document.exists() ? Optional.of(convertToEntity(document)) : Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting entity by ID: {}", e.getMessage());
            throw new RuntimeException("Failed to get entity", e);
        }
    }

    @Override
    public List<T> search(SearchCriteria criteria) {
        try {
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
            List<T> results = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                results.add(convertToEntity(document));
            }
            
            return results;
        } catch (Exception e) {
            logger.error("Error searching entities: {}", e.getMessage());
            throw new RuntimeException("Failed to search entities", e);
        }
    }

    @Override
    public CollectionReference getCollection() {
        return firestore.collection(collectionName);
    }

    @Override
    public Long count() {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collectionName).get().get();
            return (long) querySnapshot.size();
        } catch (Exception e) {
            logger.error("Error counting entities: {}", e.getMessage());
            throw new RuntimeException("Failed to count entities", e);
        }
    }

    @Override
    public Boolean deleteAll() {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collectionName).get().get();
            List<ApiFuture<WriteResult>> futures = new ArrayList<>();
            
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                futures.add(document.getReference().delete());
            }
            
            for (ApiFuture<WriteResult> future : futures) {
                future.get();
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Error deleting all entities: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        try {
            List<T> savedEntities = new ArrayList<>();
            for (T entity : entities) {
                Map<String, Object> data = convertToMap(entity);
                DocumentReference docRef = firestore.collection(collectionName).document();
                docRef.set(data).get();
                DocumentSnapshot snapshot = docRef.get().get();
                savedEntities.add(convertToEntity(snapshot));
            }
            return savedEntities;
        } catch (Exception e) {
            logger.error("Error saving multiple entities: {}", e.getMessage());
            throw new RuntimeException("Failed to save multiple entities", e);
        }
    }

    protected String getDefaultSearchField() {
        return "name";
    }
}
