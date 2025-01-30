package tools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

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

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
