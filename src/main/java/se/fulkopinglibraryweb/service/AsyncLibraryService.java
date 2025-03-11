package se.fulkopinglibraryweb.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncLibraryService<T> {
    CompletableFuture<T> findById(String id);
    CompletableFuture<List<T>> findAll();
    CompletableFuture<List<T>> findByQuery(String query);
    CompletableFuture<List<T>> findByBatch(List<String> ids);
    CompletableFuture<T> save(T item);
    CompletableFuture<List<T>> saveBatch(List<T> items);
    CompletableFuture<Void> delete(String id);
    CompletableFuture<Void> deleteBatch(List<String> ids);
    
    default CompletableFuture<List<T>> processBatch(List<T> items, BatchOperation<T> operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return operation.execute(items);
            } catch (Exception e) {
                throw new RuntimeException("Batch operation failed", e);
            }
        });
    }
}

@FunctionalInterface
interface BatchOperation<T> {
    List<T> execute(List<T> items) throws Exception;
}