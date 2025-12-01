package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.CocktailDao;
import com.filippovich.webtask.exception.DaoException;
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
    public Optional<Cocktail> findById(long id) throws  DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCocktail(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Cocktail> findAll() throws  DaoException {
        List<Cocktail> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                list.add(mapCocktail(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return list;
    }

    @Override
    public boolean save(Cocktail cocktail) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, cocktail.getName());
            preparedStatement.setString(2, cocktail.getDescription());
            preparedStatement.setString(3, cocktail.getStatus().name());
            preparedStatement.setLong(4, cocktail.getAuthor().getId());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(cocktail.getCreatedAt()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cocktail.setId(generatedKeys.getLong(1));
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Cocktail cocktail) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {

            preparedStatement.setString(1, cocktail.getName());
            preparedStatement.setString(2, cocktail.getDescription());
            preparedStatement.setString(3, cocktail.getStatus().name());
            preparedStatement.setLong(4, cocktail.getAuthor().getId());
            preparedStatement.setLong(5, cocktail.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(long id) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Cocktail> findByStatus(String status) throws DaoException {
        List<Cocktail> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_STATUS)) {

            preparedStatement.setString(1, status);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapCocktail(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return list;
    }

    private Cocktail mapCocktail(ResultSet resultSet) throws SQLException {
        Cocktail cocktail = new Cocktail();
        cocktail.setId(resultSet.getLong("id"));
        cocktail.setName(resultSet.getString("name"));
        cocktail.setDescription(resultSet.getString("description"));
        cocktail.setStatus(CocktailStatus.valueOf(resultSet.getString("status")));

        User author = new User();
        author.setId(resultSet.getLong("author_id"));
        cocktail.setAuthor(author);

        cocktail.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return cocktail;
    }
}
