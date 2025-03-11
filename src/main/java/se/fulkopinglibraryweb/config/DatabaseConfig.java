package se.fulkopinglibraryweb.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;
    private static final int MAX_POOL_SIZE = 20;
    private static final int MIN_IDLE = 5;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int IDLE_TIMEOUT = 300000; // 5 minutes
    private static final int MAX_LIFETIME = 1200000; // 20 minutes

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv("DB_URL"));
        config.setUsername(System.getenv("DB_USERNAME"));
        config.setPassword(System.getenv("DB_PASSWORD"));
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setMinimumIdle(MIN_IDLE);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setIdleTimeout(IDLE_TIMEOUT);
        config.setMaxLifetime(MAX_LIFETIME);
        config.setAutoCommit(true);
        config.addHealthCheckProperty("connectivityCheckTimeoutMs", "1000");
        config.setPoolName("FulkopingLibraryPool");

        // Enable JMX monitoring
        config.setRegisterMbeans(true);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static boolean isHealthy() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}