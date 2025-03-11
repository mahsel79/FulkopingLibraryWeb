package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AsyncCrudOperations<T, ID> {
    CompletableFuture<T> create(T entity);
    CompletableFuture<Optional<T>> read(ID id);
    CompletableFuture<T> getById(ID id);
    CompletableFuture<T> update(T entity);
    CompletableFuture<Void> delete(ID id);
    CompletableFuture<List<T>> getAll();
}
