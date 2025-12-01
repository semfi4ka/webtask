package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.connection.DatabaseConfig;
import com.filippovich.webtask.dao.UserDao;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private final DataSource dataSource;
    private final String SQL_FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private final String SQL_FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private final String SQL_FIND_ALL = "SELECT * FROM users";
    private final String SQL_SAVE = "INSERT INTO users (username, email, password, role, created_at) VALUES (?, ?, ?, ?, ?)";
    private final String SQL_UPDATE = "UPDATE users SET username=?, email=?, password=?, role=? WHERE id=?";
    private final String SQL_DELETE = "DELETE FROM users WHERE id = ?";
    private final String SQL_AUTHENTICATION = "SELECT * FROM users WHERE email = ? AND password = ?";
    public UserDaoImpl() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public Optional<User> findById(long id) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean save(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getRole().name());
            preparedStatement.setLong(5, user.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Optional<User> authentication(String email, String passwordHash) {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_AUTHENTICATION)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password"));
        u.setRole(UserRole.valueOf(rs.getString("role")));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return u;
    }
}
