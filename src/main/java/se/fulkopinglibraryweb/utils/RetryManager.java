package se.fulkopinglibraryweb.utils;

import java.util.concurrent.Callable;
import se.fulkopinglibraryweb.utils.LoggingUtils;

public class RetryManager {
    private static final LoggingUtils LOGGER = new LoggingUtils(RetryManager.class);
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_WAIT_TIME = 1000; // 1 second

    public static <T> T executeWithRetry(Callable<T> operation) throws Exception {
        int attempts = 0;
        long waitTime = INITIAL_WAIT_TIME;

        while (attempts < MAX_RETRIES) {
            try {
                return operation.call();
            } catch (Exception e) {
                attempts++;
                if (attempts == MAX_RETRIES) {
                    LOGGER.error("Operation failed after " + MAX_RETRIES + " attempts", e);
                    throw e;
                }

                LOGGER.error(String.format(
                    "Operation failed (attempt %d/%d). Retrying in %d ms",
                    attempts, MAX_RETRIES, waitTime
                ), e);

                Thread.sleep(waitTime);
                waitTime *= 2; // Exponential backoff
            }
        }

        throw new RuntimeException("Unexpected retry loop exit");
    }

    public static void executeWithRetry(RunnableWithException operation) throws Exception {
        executeWithRetry(() -> {
            operation.run();
            return null;
        });
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }
}
