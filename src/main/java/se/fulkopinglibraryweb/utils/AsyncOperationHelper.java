package se.fulkopinglibraryweb.utils;

import se.fulkopinglibraryweb.monitoring.PerformanceMonitor;
import org.slf4j.Logger;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncOperationHelper {
    private final PerformanceMonitor monitor;
    private final Logger logger;

    public AsyncOperationHelper(PerformanceMonitor monitor, Logger logger) {
        this.monitor = monitor;
        this.logger = logger;
    }

    public <T> CompletableFuture<T> executeAsync(
            String operationName,
            Supplier<T> operation,
            String errorMessage,
            String operationType,
            String entityId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                monitor.recordRequestStart(operationName);
                T result = operation.get();
                monitor.recordRequestEnd(operationName);
                return result;
            } catch (Exception e) {
                monitor.recordError(operationName);
                logger.error("{} failed: {}", operationName, e.getMessage(), e);
                throw new RuntimeException(errorMessage, e);
            }
        });
    }

    public CompletableFuture<Void> executeAsyncVoid(
            String operationName,
            Runnable operation,
            String errorMessage,
            String operationType,
            String entityId) {
        return CompletableFuture.runAsync(() -> {
            try {
                monitor.recordRequestStart(operationName);
                operation.run();
                monitor.recordRequestEnd(operationName);
            } catch (Exception e) {
                monitor.recordError(operationName);
                logger.error("{} failed: {}", operationName, e.getMessage(), e);
                throw new RuntimeException(errorMessage, e);
            }
        });
    }
}
