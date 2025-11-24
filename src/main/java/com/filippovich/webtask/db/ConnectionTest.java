package com.filippovich.webtask.db;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("УСПЕХ! Подключение к MySQL работает.");
                System.out.println("URL: " + conn.getMetaData().getURL());
                System.out.println("User: " + conn.getMetaData().getUserName());
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }
}
