package se.fulkopinglibraryweb.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CircuitBreaker {
    private static final Logger logger = Logger.getLogger(CircuitBreaker.class.getName());
    
    private final String name;
    private final int failureThreshold;
    private final long resetTimeout;
    
    private final AtomicReference<State> state;
    private final AtomicInteger failureCount;
    private volatile long lastFailureTime;
    
    public enum State {
        CLOSED,     // Normal operation, allowing requests
        OPEN,       // Circuit is open, fast-failing requests
        HALF_OPEN   // Testing if service is back online
    }
    
    public CircuitBreaker(String name, int failureThreshold, long resetTimeout) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
        this.state = new AtomicReference<>(State.CLOSED);
        this.failureCount = new AtomicInteger(0);
    }
    
    public <T> T execute(Supplier<T> operation) throws Exception {
        if (!canExecute()) {
            throw new CircuitBreakerException("Circuit breaker is open");
        }
        
        try {
            T result = operation.get();
            reset();
            return result;
        } catch (Exception e) {
            handleFailure();
            throw e;
        }
    }
    
    private boolean canExecute() {
        State currentState = state.get();
        if (currentState == State.CLOSED) {
            return true;
        }
        
        if (currentState == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime >= resetTimeout) {
                state.compareAndSet(State.OPEN, State.HALF_OPEN);
                return true;
            }
            return false;
        }
        
        return true; // HALF_OPEN state allows single request
    }
    
    private void handleFailure() {
        lastFailureTime = System.currentTimeMillis();
        if (failureCount.incrementAndGet() >= failureThreshold) {
            state.set(State.OPEN);
            logger.log(Level.WARNING, "Circuit breaker ''{0}'' is now OPEN", name);
        }
    }
    
    private void reset() {
        failureCount.set(0);
        state.set(State.CLOSED);
    }
    
    public State getState() {
        return state.get();
    }
    
    public static class CircuitBreakerException extends RuntimeException {
        public CircuitBreakerException(String message) {
            super(message);
        }
    }
}