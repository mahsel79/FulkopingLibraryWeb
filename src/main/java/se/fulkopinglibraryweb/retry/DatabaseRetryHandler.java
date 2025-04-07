package se.fulkopinglibraryweb.retry;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.function.Supplier;
import se.fulkopinglibraryweb.utils.LoggerUtil;

public class DatabaseRetryHandler {
    private final Retry retry;

    public DatabaseRetryHandler() {
        RetryConfig retryConfig = RetryConfig.<Connection>custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(100))
                .retryOnException(throwable -> throwable instanceof SQLException)
                .failAfterMaxAttempts(true)
                .build();

        RetryRegistry registry = RetryRegistry.of(retryConfig);
        retry = registry.retry("databaseRetry", retryConfig);

        retry.getEventPublisher()
                .onRetry(event -> LoggerUtil.logWarn(
                    "DatabaseRetryHandler",
                    event.getNumberOfRetryAttempts(),
                    "Retry attempt %d/%d after %s",
                    retryConfig.getMaxAttempts(),
                    event.getLastThrowable().getMessage()
                ))
                .onError(event -> LoggerUtil.logError(
                    "DatabaseRetryHandler",
                    "Retry exhausted after %d attempts: %s",
                    event.getNumberOfRetryAttempts(),
                    event.getLastThrowable().getMessage()
                ));
    }

    public <T> T executeWithRetry(Supplier<T> supplier) {
        return Retry.decorateSupplier(retry, supplier).get();
    }

    public Retry getRetry() {
        return retry;
    }
}
