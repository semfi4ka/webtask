package com.filippovich.webtask.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;

public class ConnectionDataSource {

    private static final Logger logger = LogManager.getLogger(ConnectionDataSource.class);

    private static final HikariDataSource dataSource;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/webtask_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final int MINIMUM_IDLE = 2;
    private static final int CONNECTION_TIME_OUT = 30000;
    private static final int IDLE_TIME_OUT = 600000;
    private static final int MAX_LIFE_TIME = 1800000;
    public static final String COM_MYSQL_CJ_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            logger.info("Loading MySQL JDBC Driver...");
            Class.forName(COM_MYSQL_CJ_JDBC_DRIVER);
            logger.info("MySQL JDBC Driver loaded successfully.");

            logger.info("Initializing HikariCP DataSource...");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(JDBC_URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
            config.setMinimumIdle(MINIMUM_IDLE);
            config.setConnectionTimeout(CONNECTION_TIME_OUT);
            config.setIdleTimeout(IDLE_TIME_OUT);
            config.setMaxLifetime(MAX_LIFE_TIME);

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP DataSource initialized successfully.");

        } catch (ClassNotFoundException e) {
            logger.fatal("MySQL JDBC Driver not found!", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
