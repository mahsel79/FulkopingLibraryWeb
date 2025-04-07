package se.fulkopinglibraryweb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class for standardized logging throughout the application.
 * Provides methods for all log levels with support for:
 * - Class-based and named loggers
 * - Exception logging
 * - MDC context logging
 * - Safe object string conversion
 * - Varargs parameter handling
 */
public class LoggerUtil {

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    // Info logging methods
    public static void logInfo(Class<?> clazz, Object message) {
        getLogger(clazz).info(safeToString(message));
    }
    
    public static void logInfo(Class<?> clazz, String message) {
        getLogger(clazz).info(message);
    }
    
    public static void logInfo(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).info(message, args);
    }
    
    public static void logInfo(Class<?> clazz, Object obj1, String message, Object... args) {
        getLogger(clazz).info(message, prependArg(obj1, args));
    }
    
    public static void logInfo(String name, Object message) {
        getLogger(name).info(safeToString(message));
    }
    
    public static void logInfo(String name) {
        getLogger(name).info("");
    }
    
    public static void logInfo(String name, String message) {
        getLogger(name).info(message);
    }
    
    public static void logInfo(String name, String message, Object... args) {
        getLogger(name).info(message, args);
    }

    // Error logging methods
    public static void logError(Class<?> clazz, Object message) {
        getLogger(clazz).error(safeToString(message));
    }
    
    public static void logError(Class<?> clazz, String message) {
        getLogger(clazz).error(message);
    }
    
    public static void logError(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).error(message, args);
    }
    
    public static void logError(Class<?> clazz, Object obj1, String message, Object... args) {
        getLogger(clazz).error(message, prependArg(obj1, args));
    }
    
    public static void logError(String name, Object message) {
        getLogger(name).error(safeToString(message));
    }
    
    public static void logError(String name, String message) {
        getLogger(name).error(message);
    }
    
    public static void logError(String name, String message, Object... args) {
        getLogger(name).error(message, args);
    }
    
    public static void logError(String name, String format, Object arg1, Object arg2) {
        getLogger(name).error(format, arg1, arg2);
    }
    
    public static void logError(String name, String format, Object arg1, Object arg2, Object arg3) {
        getLogger(name).error(format, arg1, arg2, arg3);
    }

    // Debug logging methods
    public static void logDebug(Class<?> clazz, Object message) {
        getLogger(clazz).debug(safeToString(message));
    }
    
    public static void logDebug(Class<?> clazz, String message) {
        getLogger(clazz).debug(message);
    }
    
    public static void logDebug(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).debug(message, args);
    }
    
    public static void logDebug(Class<?> clazz, Object obj1, String message, Object... args) {
        getLogger(clazz).debug(message, prependArg(obj1, args));
    }
    
    public static void logDebug(String name, Object message) {
        getLogger(name).debug(safeToString(message));
    }
    
    public static void logDebug(String name, String message) {
        getLogger(name).debug(message);
    }
    
    public static void logDebug(String name) {
        getLogger(name).debug("");
    }
    
    public static void logDebug(String name, String message, Object... args) {
        getLogger(name).debug(message, args);
    }

    // Warning logging methods
    public static void logWarn(String name, Object message) {
        getLogger(name).warn(safeToString(message));
    }
    
    public static void logWarn(String name, String message) {
        getLogger(name).warn(message);
    }
    
    public static void logWarn(String name) {
        getLogger(name).warn("");
    }
    
    public static void logWarn(String name, String message, Object... args) {
        getLogger(name).warn(message, args);
    }
    
    public static void logWarn(String name, Object obj1, String message, Object... args) {
        getLogger(name).warn(message, prependArg(obj1, args));
    }

    // Trace logging methods
    public static void logTrace(Class<?> clazz, String message) {
        getLogger(clazz).trace(message);
    }
    
    public static void logTrace(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).trace(message, args);
    }
    
    public static void logTrace(String name, String message) {
        getLogger(name).trace(message);
    }
    
    public static void logTrace(String name, String message, Object... args) {
        getLogger(name).trace(message, args);
    }

    // Exception logging methods
    public static void logError(Class<?> clazz, String message, Throwable t) {
        getLogger(clazz).error(message, t);
    }
    
    public static void logError(String name, String message, Throwable t) {
        getLogger(name).error(message, t);
    }
    
    public static void logWarn(Class<?> clazz, String message, Throwable t) {
        getLogger(clazz).warn(message, t);
    }
    
    public static void logWarn(String name, String message, Throwable t) {
        getLogger(name).warn(message, t);
    }

    // MDC context methods
    public static void putMdc(String key, String value) {
        MDC.put(key, value);
    }
    
    public static void removeMdc(String key) {
        MDC.remove(key);
    }
    
    public static void clearMdc() {
        MDC.clear();
    }
    
    public static Map<String, String> getMdcContext() {
        return MDC.getCopyOfContextMap();
    }

    // Enhanced safe string conversion
    private static String safeToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj.getClass().isArray()) {
            return arrayToString(obj);
        }
        if (obj instanceof Collection) {
            return collectionToString((Collection<?>) obj);
        }
        return obj.toString();
    }

    private static String arrayToString(Object array) {
        if (array instanceof Object[]) {
            return Arrays.toString((Object[]) array);
        }
        if (array instanceof int[]) {
            return Arrays.toString((int[]) array);
        }
        if (array instanceof long[]) {
            return Arrays.toString((long[]) array);
        }
        if (array instanceof double[]) {
            return Arrays.toString((double[]) array);
        }
        if (array instanceof boolean[]) {
            return Arrays.toString((boolean[]) array);
        }
        if (array instanceof char[]) {
            return Arrays.toString((char[]) array);
        }
        if (array instanceof byte[]) {
            return Arrays.toString((byte[]) array);
        }
        if (array instanceof short[]) {
            return Arrays.toString((short[]) array);
        }
        if (array instanceof float[]) {
            return Arrays.toString((float[]) array);
        }
        return array.toString();
    }

    private static String collectionToString(Collection<?> collection) {
        return collection.stream()
            .map(LoggerUtil::safeToString)
            .collect(java.util.stream.Collectors.joining(", ", "[", "]"));
    }

    // Stream-based prepend methods
    private static Object[] prependArg(Object obj, Object... args) {
        return java.util.stream.Stream.concat(
            java.util.stream.Stream.of(obj),
            args != null ? java.util.stream.Stream.of(args) : java.util.stream.Stream.empty()
        ).toArray();
    }
    
    private static Object[] prependArgs(Object[] objs, Object... args) {
        return java.util.stream.Stream.concat(
            java.util.stream.Stream.of(objs),
            args != null ? java.util.stream.Stream.of(args) : java.util.stream.Stream.empty()
        ).toArray();
    }
}
