package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AsyncGenericService<T, DTO, ID> {
    CompletableFuture<T> save(DTO dto);
    CompletableFuture<Optional<T>> findById(ID id);
    CompletableFuture<T> getById(ID id);
    CompletableFuture<T> read(ID id);
    CompletableFuture<T> update(ID id, DTO dto);
    CompletableFuture<Boolean> deleteById(ID id);
    CompletableFuture<List<DTO>> findAll();
    CompletableFuture<Boolean> existsById(ID id);
    CompletableFuture<Long> count();
    
    CompletableFuture<Map<String, Object>> convertEntityToMap(T entity);
    CompletableFuture<Map<String, Object>> convertDtoToMap(DTO dto);
}
