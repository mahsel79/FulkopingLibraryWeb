package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenericService<T, ID> {
    Optional<T> save(T entity);
    T findById(ID id);
    T getById(ID id);
    T read(ID id);
    List<T> findAll();
    Optional<T> update(T entity);
    Optional<T> update(ID id, T entity);
    boolean deleteById(ID id);
    boolean existsById(ID id);
    long count();
    
    // Conversion method for entity to map
    Map<String, Object> convertEntityToMap(T entity);
}
