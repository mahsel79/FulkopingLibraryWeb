package se.fulkopinglibraryweb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.time.Instant;
import java.time.Duration;

public class LoggingUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.class);
    private static FileHandler fileHandler;
    private static final String LOG_FILE = "library_app.log";
    
    static {
        try {
            fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            java.util.logging.Logger.getLogger("").addHandler(fileHandler);
            java.util.logging.Logger.getLogger("").setLevel(Level.ALL);
        } catch (IOException e) {
            LOGGER.error("Failed to initialize logging: {}", e.getMessage());
        }
    }

    // Instance-based logging
    private final Logger logger;

    public LoggingUtils(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void info(String message) {
        logger.info(formatMessage(message));
    }

    public void error(String message, Throwable throwable) {
        logger.error(formatMessage(message), throwable);
    }

    public void error(String message, Object param, Throwable throwable) {
        logger.error(formatMessage(String.format(message, param)), throwable);
    }

    public void debug(String message) {
        logger.debug(formatMessage(message));
    }

    public void logMethodEntry(String methodName) {
        logger.info(formatMessage("Entering method: " + methodName));
    }

    public <T> void logMethodEntry(String methodName, T param) {
        logger.info(formatMessage(String.format("Entering method: %s with param: %s", methodName, param)));
    }

    public <T, U> void logMethodEntry(String methodName, T param1, U param2) {
        logger.info(formatMessage(String.format("Entering method: %s with params: %s, %s", methodName, param1, param2)));
    }

    // Static utility methods
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void logInfo(Logger logger, String message) {
        logger.info(formatMessage(message));
    }

    public static void logError(Logger logger, String message, Throwable throwable) {
        logger.error(formatMessage(message), throwable);
    }

    public static void logError(Logger logger, String message, Object param, Throwable throwable) {
        logger.error(formatMessage(String.format(message, param)), throwable);
    }

    public static void logDebug(Logger logger, String message) {
        logger.debug(formatMessage(message));
    }

    public static void logWarn(Logger logger, String message) {
        logger.warn(formatMessage(message));
    }

    public static void logServiceOperation(String className, String methodName, String message) {
        LOGGER.info("[{}] {} - {}", className, methodName, message);
    }

    // Performance monitoring
    private static ThreadLocal<Instant> operationStart = new ThreadLocal<>();

    public static void startOperation(String operationName) {
        operationStart.set(Instant.now());
        LOGGER.info(formatMessage("Starting operation: " + operationName));
    }

    public static void endOperation(String operationName) {
        Instant start = operationStart.get();
        if (start != null) {
            Duration duration = Duration.between(start, Instant.now());
            LOGGER.info(formatMessage(String.format("Operation '%s' completed in %d ms", operationName, duration.toMillis())));
            operationStart.remove();
        }
    }

    private static String formatMessage(String message) {
        return String.format("[%s] %s", Thread.currentThread().getName(), message);
    }

    // Method to track specific metrics
    public static void logMetric(String metricName, long value) {
        LOGGER.info(formatMessage(String.format("METRIC - %s: %d", metricName, value)));
    }

    // Method for security-related logs
    public static void logSecurityEvent(String event, String username) {
        LOGGER.info(formatMessage(String.format("SECURITY - User: %s, Event: %s", username, event)));
    }

    // Method for database operation logs
    public static void logDatabaseOperation(String operation, String details) {
        LOGGER.info(formatMessage(String.format("DATABASE - Operation: %s, Details: %s", operation, details)));
    }

    // Cleanup method to be called on application shutdown
    public static void cleanup() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }

    // Method entry logging
    public static void logMethodEntry(Logger logger, String methodName) {
        logger.info(formatMessage("Entering method: " + methodName));
    }

    public static <T> void logMethodEntry(Logger logger, String methodName, T param) {
        logger.info(formatMessage(String.format("Entering method: %s with param: %s", methodName, param)));
    }

    public static <T, U> void logMethodEntry(Logger logger, String methodName, T param1, U param2) {
        logger.info(formatMessage(String.format("Entering method: %s with params: %s, %s", methodName, param1, param2)));
    }

    // Method exit logging
    public static <T> void logMethodExit(Logger logger, String methodName, Optional<T> result) {
        logger.info(formatMessage(String.format("Exiting method: %s with result: %s", methodName, result.orElse(null))));
    }

    public static <T> void logMethodExit(Logger logger, String methodName, T result) {
        logger.info(formatMessage(String.format("Exiting method: %s with result: %s", methodName, result)));
    }
}
