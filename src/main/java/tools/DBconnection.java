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
        config.setUsername("root");
        config.setPassword("root");
        config.setMaximumPoolSize(10); // Nombre maximum de connexions dans le pool
        config.setMinimumIdle(2); // Nombre minimum de connexions inactives
        config.setIdleTimeout(30000); // Temps d'inactivité avant fermeture (30 sec)
        config.setMaxLifetime(1800000); // Durée de vie maximale d'une connexion (30 min)
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
