package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.CocktailIngredientDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.CocktailIngredient;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CocktailIngredientDaoImpl implements CocktailIngredientDao {

    private static final String SQL_INSERT =
            "INSERT INTO cocktail_ingredients (cocktail_id, ingredient_id, amount) VALUES (?, ?, ?)";

    private final DataSource dataSource;

    public CocktailIngredientDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveIngredients(long cocktailId, List<CocktailIngredient> ingredients) throws DaoException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            for (CocktailIngredient ci : ingredients) {
                ps.setLong(1, cocktailId);
                ps.setLong(2, ci.getIngredient().getId());
                ps.setDouble(3, ci.getAmount());
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
