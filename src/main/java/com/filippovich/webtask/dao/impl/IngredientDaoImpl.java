package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.IngredientDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Ingredient;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientDaoImpl implements IngredientDao {

    public static final String SQL_FIND_BY_ID = "SELECT * FROM ingredients WHERE id=?";
    public static final String SQL_FIND_BY_NAME = "SELECT * FROM ingredients WHERE name=?";
    public static final String SQL_FIND_ALL = "SELECT * FROM ingredients";
    public static final String SQL_SAVE = "INSERT INTO ingredients (name, unit) VALUES (?, ?)";
    public static final String SQL_UPDATE = "UPDATE ingredients SET name=?, unit=? WHERE id=?";
    public static final String SQL_DELETE = "DELETE FROM ingredients WHERE id=?";
    private final DataSource dataSource;

    public IngredientDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Ingredient> findById(long id) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapIngredient(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Ingredient> findByName(String name) throws DaoException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_NAME)) {

            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapIngredient(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Ingredient> findAll() throws DaoException {
        List<Ingredient> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                list.add(mapIngredient(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return list;
    }

    @Override
    public boolean save(Ingredient ingredient) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, ingredient.getName());
            preparedStatement.setString(2, ingredient.getUnit());

            int affected = preparedStatement.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ingredient.setId(generatedKeys.getLong(1));
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Ingredient ingredient) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {

            preparedStatement.setString(1, ingredient.getName());
            preparedStatement.setString(2, ingredient.getUnit());
            preparedStatement.setLong(3, ingredient.getId());

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

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        Ingredient i = new Ingredient();
        i.setId(rs.getLong("id"));
        i.setName(rs.getString("name"));
        i.setUnit(rs.getString("unit"));
        return i;
    }
}
