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
    List<T> findByFieldSync(String field, Object value);
    T update(ID id, Map<String, Object> updates);
    CollectionReference getCollection();
    List<T> search(SearchCriteria criteria);
    Long count();
    Boolean deleteAll();
    List<T> saveAll(List<T> entities);
}
