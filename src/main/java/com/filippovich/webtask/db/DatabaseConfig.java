package com.filippovich.webtask.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://localhost:3306/webtask_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
        config.setUsername("root");
        config.setPassword("1234");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
