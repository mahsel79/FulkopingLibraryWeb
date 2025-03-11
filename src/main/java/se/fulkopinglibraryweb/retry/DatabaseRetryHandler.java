package se.fulkopinglibraryweb.retry;

import org.slf4j.Logger;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.function.Supplier;
import se.fulkopinglibraryweb.utils.LoggingUtils;

public class DatabaseRetryHandler {
    private static final Logger LOGGER = LoggingUtils.getLogger(DatabaseRetryHandler.class);
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
                .onRetry(event -> LoggingUtils.logWarn(LOGGER, String.format(
                    "Retry attempt %d/%d after %s",
                    event.getNumberOfRetryAttempts(),
                    retryConfig.getMaxAttempts(),
                    event.getLastThrowable().getMessage()
                )))
                .onError(event -> LoggingUtils.logError(LOGGER, 
                    String.format("Retry exhausted after %d attempts", event.getNumberOfRetryAttempts()),
                    event.getLastThrowable()
                ));
    }

    public <T> T executeWithRetry(Supplier<T> supplier) {
        return Retry.decorateSupplier(retry, supplier).get();
    }

    public Retry getRetry() {
        return retry;
    }
}
