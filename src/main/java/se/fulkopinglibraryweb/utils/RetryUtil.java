package se.fulkopinglibraryweb.utils;

import java.util.concurrent.Callable;
import java.util.function.Predicate;
import se.fulkopinglibraryweb.utils.LoggerUtil;

public class RetryUtil {
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF = 1000L; // 1 second

    public static <T> T executeWithRetry(Callable<T> operation) throws Exception {
        return executeWithRetry(operation, MAX_RETRIES, INITIAL_BACKOFF, e -> true);
    }

    public static <T> T executeWithRetry(Callable<T> operation, int maxRetries, long initialBackoff,
                                        Predicate<Exception> retryableException) throws Exception {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            try {
                return operation.call();
            } catch (Exception e) {
                lastException = e;
                if (!retryableException.test(e)) {
                    throw e;
                }

                attempts++;
                if (attempts >= maxRetries) {
                    break;
                }

                long backoffTime = calculateBackoffTime(initialBackoff, attempts);
                LoggerUtil.logWarn("RetryUtil", "Operation failed, attempt {} of {}. Retrying in {} ms...",
                        attempts, maxRetries, backoffTime);

                try {
                    Thread.sleep(backoffTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }

        throw new RuntimeException("Operation failed after " + maxRetries + " attempts", lastException);
    }

    private static long calculateBackoffTime(long initialBackoff, int attempt) {
        return (long) (initialBackoff * Math.pow(2, attempt - 1));
    }
}
