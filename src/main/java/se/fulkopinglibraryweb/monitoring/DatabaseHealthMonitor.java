package se.fulkopinglibraryweb.monitoring;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHealthMonitor {
    private static final Logger LOGGER = Logger.getLogger(DatabaseHealthMonitor.class.getName());
    private final HikariDataSource dataSource;
    private final ScheduledExecutorService healthCheckExecutor;
    private ScheduledFuture<?> healthCheckFuture;
    private static final int HEALTH_CHECK_INTERVAL_SECONDS = 30;
    private static final int CONNECTION_VALIDATION_TIMEOUT_SECONDS = 5;

    public DatabaseHealthMonitor(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.healthCheckExecutor = new ScheduledThreadPoolExecutor(1);
    }

    public void startMonitoring() {
        healthCheckFuture = healthCheckExecutor.scheduleAtFixedRate(
            this::performHealthCheck,
            0,
            HEALTH_CHECK_INTERVAL_SECONDS,
            TimeUnit.SECONDS
        );
        LOGGER.info("Database health monitoring started");
    }

    private void performHealthCheck() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(CONNECTION_VALIDATION_TIMEOUT_SECONDS)) {
                LOGGER.info("Database connection health check: OK");
                logPoolStats();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection health check failed", e);
            logPoolStats();
        }
    }

    private void logPoolStats() {
        LOGGER.info(String.format(
            "Pool Stats - Active: %d, Idle: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        ));
    }

    public void stopMonitoring() {
        if (healthCheckFuture != null) {
            healthCheckFuture.cancel(true);
        }
        if (healthCheckExecutor != null) {
            healthCheckExecutor.shutdown();
            try {
                if (!healthCheckExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    healthCheckExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                healthCheckExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("Database health monitoring stopped");
    }
}