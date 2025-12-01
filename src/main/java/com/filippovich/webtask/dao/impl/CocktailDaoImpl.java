package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.CocktailDao;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailStatus;
import com.filippovich.webtask.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CocktailDaoImpl implements CocktailDao {

    public static final String SQL_FIND_BY_ID = "SELECT * FROM cocktails WHERE id = ?";
    public static final String SQL_FIND_ALL = "SELECT * FROM cocktails";
    public static final String SQL_SAVE = """
            INSERT INTO cocktails (name, description, status, author_id, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;
    public static final String SQL_UPDATE = """
            UPDATE cocktails
            SET name=?, description=?, status=?, author_id=?
            WHERE id=?
            """;
    public static final String SQL_DELETE = "DELETE FROM cocktails WHERE id=?";
    public static final String SQL_FIND_BY_STATUS = "SELECT * FROM cocktails WHERE status=?";
    private final DataSource dataSource;

    public CocktailDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Cocktail> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCocktail(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Cocktail> findAll() {
        List<Cocktail> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapCocktail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean save(Cocktail cocktail) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cocktail.getName());
            ps.setString(2, cocktail.getDescription());
            ps.setString(3, cocktail.getStatus().name());
            ps.setLong(4, cocktail.getAuthor().getId());
            ps.setTimestamp(5, Timestamp.valueOf(cocktail.getCreatedAt()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cocktail.setId(generatedKeys.getLong(1));
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Cocktail cocktail) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, cocktail.getName());
            ps.setString(2, cocktail.getDescription());
            ps.setString(3, cocktail.getStatus().name());
            ps.setLong(4, cocktail.getAuthor().getId());
            ps.setLong(5, cocktail.getId());

            return ps.executeUpdate() > 0;

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
    public List<Cocktail> findByStatus(String status) {
        List<Cocktail> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_STATUS)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCocktail(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Cocktail mapCocktail(ResultSet rs) throws SQLException {
        Cocktail c = new Cocktail();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setStatus(CocktailStatus.valueOf(rs.getString("status")));

        User author = new User();
        author.setId(rs.getLong("author_id"));
        c.setAuthor(author);

        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return c;
    }
}
