package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;
import java.util.Optional;

public interface CrudOperations<T, ID> {
    T create(T entity);
    Optional<T> read(ID id);
    T update(T entity);
    void delete(ID id);
    List<T> getAll();
}
