package tools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database connections using a HikariCP connection pool. This class
 * initializes a static {@link HikariDataSource} and provides a method to
 * retrieve connections from the pool.
 */
public class DBconnection {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3307/h2online?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        //config.setJdbcUrl("jdbc:mysql://localhost:3307/h2online?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8mb4");
        //config.setJdbcUrl("jdbc:mysql://localhost:3307/h2online?useSSL=false&serverTimezone=UTC");

        config.setUsername("root");
        config.setPassword("root");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(1800000);
        dataSource = new HikariDataSource(config);
    }

    /**
     * Retrieves a database connection from the HikariCP connection pool.
     *
     * @return A {@link Connection} to the database.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
