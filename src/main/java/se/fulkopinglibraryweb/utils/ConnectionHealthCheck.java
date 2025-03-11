package se.fulkopinglibraryweb.utils;

import se.fulkopinglibraryweb.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHealthCheck {
    private static final Logger LOGGER = Logger.getLogger(ConnectionHealthCheck.class.getName());
    private static final String HEALTH_CHECK_QUERY = "SELECT 1";
    private static final int INITIAL_DELAY = 0;
    private static final int CHECK_INTERVAL = 60; // seconds
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void startHealthCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                performHealthCheck();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Health check failed", e);
            }
        }, INITIAL_DELAY, CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    private static int consecutiveFailures = 0;
    private static final int MAX_CONSECUTIVE_FAILURES = 3;

    private static void performHealthCheck() {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(HEALTH_CHECK_QUERY)) {
            
            stmt.execute();
            LOGGER.info("Database connection health check passed");
            
            // Reset failure counter on success
            if (consecutiveFailures > 0) {
                consecutiveFailures = 0;
                LOGGER.info("Database connection restored after failures");
            }
            
            // Log pool statistics
            logPoolStatistics();
            
        } catch (SQLException e) {
            consecutiveFailures++;
            LOGGER.log(Level.SEVERE, "Database connection health check failed", e);
            
            if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                LOGGER.severe(String.format(
                    "ALERT: Database connection has failed %d consecutive times!",
                    consecutiveFailures
                ));
                // TODO: Add actual alerting (email, SMS, etc) here
            }
        }
    }

    private static void logPoolStatistics() {
        var dataSource = DatabaseConfig.getDataSource();
        var pool = dataSource.getHikariPoolMXBean();
        LOGGER.info(String.format(
            "Pool Stats - Active: %d, Idle: %d, Waiting: %d, Total: %d",
            pool.getActiveConnections(),
            pool.getIdleConnections(),
            pool.getThreadsAwaitingConnection(),
            pool.getTotalConnections()
        ));
    }

    public static void stopHealthCheck() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
