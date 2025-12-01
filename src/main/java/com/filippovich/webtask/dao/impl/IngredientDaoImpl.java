package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.IngredientDao;
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
    public Optional<Ingredient> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapIngredient(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Ingredient> findByName(String name) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_NAME)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapIngredient(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Ingredient> findAll() {
        List<Ingredient> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapIngredient(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean save(Ingredient ingredient) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ingredient.getName());
            ps.setString(2, ingredient.getUnit());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ingredient.setId(generatedKeys.getLong(1));
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Ingredient ingredient) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, ingredient.getName());
            ps.setString(2, ingredient.getUnit());
            ps.setLong(3, ingredient.getId());

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

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        Ingredient i = new Ingredient();
        i.setId(rs.getLong("id"));
        i.setName(rs.getString("name"));
        i.setUnit(rs.getString("unit"));
        return i;
    }
}
