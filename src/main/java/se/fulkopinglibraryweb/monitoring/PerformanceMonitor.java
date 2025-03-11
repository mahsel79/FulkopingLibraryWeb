package se.fulkopinglibraryweb.monitoring;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.time.Instant;

public class PerformanceMonitor {
    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    private final ConcurrentHashMap<String, LongAdder> requestCounts;
    private final ConcurrentHashMap<String, LongAdder> errorCounts;
    private final ConcurrentHashMap<String, AtomicLong> responseTimeTotals;
    private final ConcurrentHashMap<String, AtomicLong> lastExecutionTime;

    private PerformanceMonitor() {
        this.requestCounts = new ConcurrentHashMap<>();
        this.errorCounts = new ConcurrentHashMap<>();
        this.responseTimeTotals = new ConcurrentHashMap<>();
        this.lastExecutionTime = new ConcurrentHashMap<>();
    }

    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }

    public void recordRequestStart(String operationType) {
        requestCounts.computeIfAbsent(operationType, k -> new LongAdder()).increment();
        lastExecutionTime.computeIfAbsent(operationType, k -> new AtomicLong())
                        .set(Instant.now().toEpochMilli());
    }

    public void recordRequestEnd(String operationType) {
        long startTime = lastExecutionTime.get(operationType).get();
        long duration = Instant.now().toEpochMilli() - startTime;
        responseTimeTotals.computeIfAbsent(operationType, k -> new AtomicLong())
                         .addAndGet(duration);
    }

    public void recordError(String operationType) {
        errorCounts.computeIfAbsent(operationType, k -> new LongAdder()).increment();
    }

    public long getRequestCount(String operationType) {
        return requestCounts.getOrDefault(operationType, new LongAdder()).sum();
    }

    public long getErrorCount(String operationType) {
        return errorCounts.getOrDefault(operationType, new LongAdder()).sum();
    }

    public double getAverageResponseTime(String operationType) {
        long totalTime = responseTimeTotals.getOrDefault(operationType, new AtomicLong()).get();
        long totalRequests = getRequestCount(operationType);
        return totalRequests > 0 ? (double) totalTime / totalRequests : 0.0;
    }

    public void reset(String operationType) {
        requestCounts.remove(operationType);
        errorCounts.remove(operationType);
        responseTimeTotals.remove(operationType);
        lastExecutionTime.remove(operationType);
    }
}