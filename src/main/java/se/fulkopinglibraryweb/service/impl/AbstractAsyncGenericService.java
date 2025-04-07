package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.repository.FirestoreRepository;
import se.fulkopinglibraryweb.service.interfaces.AsyncGenericService;
import se.fulkopinglibraryweb.monitoring.PerformanceMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAsyncGenericService<T, DTO, ID, R extends FirestoreRepository<T, ID>> 
    implements AsyncGenericService<T, DTO, ID> {

    protected final R repository;
    protected final PerformanceMonitor monitor;
    protected final Logger logger;

    public AbstractAsyncGenericService(R repository) {
        this.repository = repository;
        this.monitor = PerformanceMonitor.getInstance();
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public CompletableFuture<T> save(DTO dto) {
        return convertDtoToEntity(dto).thenCompose(this::saveEntity);
    }

    protected CompletableFuture<T> saveEntity(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart("save");
                T saved = repository.save(entity);
                monitor.recordRequestEnd("save");
                return saved;
            } catch (Exception e) {
                monitor.recordError("save");
                logger.error("Failed to save entity: {}", e.getMessage(), e);
                throw wrapException("Failed to save entity", "save", null, e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<T>> findById(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart("findById");
                Optional<T> entity = repository.findById(id);
                monitor.recordRequestEnd("findById");
                return entity;
            } catch (Exception e) {
                monitor.recordError("findById");
                logger.error("Failed to find entity with id {}: {}", id, e.getMessage(), e);
                throw wrapException("Failed to find entity", "findById", id, e);
            }
        });
    }

    @Override
    public CompletableFuture<T> getById(ID id) {
        return findById(id).thenApply(opt -> 
            opt.orElseThrow(() -> wrapException("Entity not found", "getById", id, null)));
    }

    @Override
    public CompletableFuture<T> read(ID id) {
        return getById(id);
    }

    @Override
    public CompletableFuture<T> update(ID id, DTO dto) {
        return convertDtoToEntity(dto).thenCompose(entity -> updateEntity(id, entity));
    }

    protected CompletableFuture<T> updateEntity(ID id, T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart("update");
                if (!repository.existsById(id)) {
                    throw new RuntimeException("Entity not found");
                }
                T updated = repository.save(entity);
                monitor.recordRequestEnd("update");
                return updated;
            } catch (Exception e) {
                monitor.recordError("update");
                logger.error("Failed to update entity with id {}: {}", id, e.getMessage(), e);
                throw wrapException("Failed to update entity", "update", id, e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart("delete");
                boolean existed = repository.existsById(id);
                if (existed) {
                    repository.deleteById(id);
                }
                monitor.recordRequestEnd("delete");
                return existed;
            } catch (Exception e) {
                monitor.recordError("delete");
                logger.error("Failed to delete entity with id {}: {}", id, e.getMessage(), e);
                throw wrapException("Failed to delete entity", "delete", id, e);
            }
        });
    }

    @Override
    public CompletableFuture<List<DTO>> findAll() {
        return findAllEntities().thenCompose(entities -> 
            convertEntitiesToDtos(entities));
    }

    protected CompletableFuture<List<T>> findAllEntities() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart("findAll");
                List<T> entities = repository.findAll();
                monitor.recordRequestEnd("findAll");
                return entities;
            } catch (Exception e) {
                monitor.recordError("findAll");
                logger.error("Failed to find all entities: {}", e.getMessage(), e);
                throw wrapException("Failed to find all entities", "findAll", null, e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> existsById(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart("exists");
                boolean exists = repository.existsById(id);
                monitor.recordRequestEnd("exists");
                return exists;
            } catch (Exception e) {
                monitor.recordError("exists");
                logger.error("Failed to check existence for id {}: {}", id, e.getMessage(), e);
                throw wrapException("Failed to check existence", "exists", id, e);
            }
        });
    }

    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart("count");
                long count = repository.count();
                monitor.recordRequestEnd("count");
                return count;
            } catch (Exception e) {
                monitor.recordError("count");
                logger.error("Failed to count entities: {}", e.getMessage(), e);
                throw wrapException("Failed to count entities", "count", null, e);
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, Object>> convertEntityToMap(T entity) {
        return convertEntityToDto(entity)
            .thenApply(dto -> {
                Map<String, Object> map = new HashMap<>();
                map.put("entity", dto);
                return map;
            });
    }

    @Override
    public CompletableFuture<Map<String, Object>> convertDtoToMap(DTO dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("dto", dto);
        return CompletableFuture.completedFuture(map);
    }

    protected abstract CompletableFuture<T> convertDtoToEntity(DTO dto);
    protected abstract CompletableFuture<DTO> convertEntityToDto(T entity);
    protected abstract CompletableFuture<List<DTO>> convertEntitiesToDtos(List<T> entities);
    protected abstract RuntimeException wrapException(String message, String operation, ID id, Exception cause);
}
