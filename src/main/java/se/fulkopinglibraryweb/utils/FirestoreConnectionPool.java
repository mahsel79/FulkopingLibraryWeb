package se.fulkopinglibraryweb.utils;

import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A connection pool implementation for Firestore connections.
 * Manages a pool of Firestore instances to optimize resource usage and improve performance.
 */
public class FirestoreConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreConnectionPool.class);
    private static int MAX_POOL_SIZE = 10;
    private static int INITIAL_POOL_SIZE = 5;
    private static long CONNECTION_TIMEOUT = 5000; // 5 seconds

    private static FirestoreConnectionPool instance;
    private final BlockingQueue<Firestore> connectionPool;
    private final AtomicInteger activeConnections;

    private FirestoreConnectionPool() {
        this.connectionPool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        this.activeConnections = new AtomicInteger(0);
        initializePool();
    }
    
    /**
     * Configures the connection pool with the specified parameters.
     * Must be called before getInstance() to take effect.
     *
     * @param initialSize The initial number of connections in the pool
     * @param maxSize The maximum number of connections in the pool
     * @param timeout The timeout in milliseconds for getting a connection
     */
    public static void configure(int initialSize, int maxSize, long timeout) {
        if (instance == null) {
            INITIAL_POOL_SIZE = initialSize;
            MAX_POOL_SIZE = maxSize;
            CONNECTION_TIMEOUT = timeout;
            logger.info("Configured connection pool with initialSize={}, maxSize={}, timeout={}ms", 
                    initialSize, maxSize, timeout);
        } else {
            logger.warn("Cannot configure connection pool after it has been initialized");
        }
    }

    public static synchronized FirestoreConnectionPool getInstance() {
        if (instance == null) {
            instance = new FirestoreConnectionPool();
        }
        return instance;
    }

    private void initializePool() {
        logger.info("Initializing Firestore connection pool with {} connections", INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            try {
                Firestore connection = FirestoreConfig.getInstance();
                connectionPool.offer(connection);
                activeConnections.incrementAndGet();
                logger.debug("Added new Firestore connection to pool. Active connections: {}", activeConnections.get());
            } catch (Exception e) {
                logger.error("Error initializing Firestore connection: {}", e.getMessage());
            }
        }
    }

    /**
     * Retrieves a Firestore connection from the pool.
     * If the pool is empty and hasn't reached MAX_POOL_SIZE, creates a new connection.
     *
     * @return A Firestore instance
     * @throws RuntimeException if unable to get a connection
     */
    public Firestore getConnection() {
        try {
            Firestore connection = connectionPool.poll(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            if (connection == null && activeConnections.get() < MAX_POOL_SIZE) {
                connection = createNewConnection();
            }
            if (connection == null) {
                throw new RuntimeException("Unable to get Firestore connection from pool");
            }
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Firestore connection", e);
        }
    }

    /**
     * Returns a connection to the pool.
     *
     * @param connection The Firestore connection to return
     */
    public void releaseConnection(Firestore connection) {
        if (connection != null) {
            connectionPool.offer(connection);
            logger.debug("Released Firestore connection back to pool. Available connections: {}", connectionPool.size());
        }
    }

    private synchronized Firestore createNewConnection() {
        if (activeConnections.get() < MAX_POOL_SIZE) {
            try {
                Firestore connection = FirestoreConfig.getInstance();
                activeConnections.incrementAndGet();
                logger.info("Created new Firestore connection. Active connections: {}", activeConnections.get());
                return connection;
            } catch (Exception e) {
                logger.error("Error creating new Firestore connection: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * Closes all connections in the pool.
     */
    public void shutdown() {
        logger.info("Shutting down Firestore connection pool");
        connectionPool.clear();
        activeConnections.set(0);
    }

    /**
     * Gets the current number of active connections.
     *
     * @return The number of active connections
     */
    public int getActiveConnectionCount() {
        return activeConnections.get();
    }

    /**
     * Gets the current number of available connections in the pool.
     *
     * @return The number of available connections
     */
    public int getAvailableConnectionCount() {
        return connectionPool.size();
    }
}
