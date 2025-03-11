package se.fulkopinglibraryweb.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FirestoreRepository<T, ID> extends GenericRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    Boolean deleteById(ID id);
    List<T> findByField(String field, Object value);
    List<T> findByFieldSync(String field, String value);
    List<T> findByFieldSync(String field, int value);
    Optional<T> findByIdSync(String id);
    T update(ID id, Map<String, Object> updates);
    T convertToEntity(DocumentSnapshot document);
    Map<String, Object> convertToMap(T entity);
    CollectionReference getCollection();
    
    // Search method returning list
    List<T> search(SearchCriteria criteria);
    
    List<String> getSearchableFields();
    Optional<T> getById(ID id);
}
