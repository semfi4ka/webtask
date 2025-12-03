package com.filippovich.webtask.dao;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.CocktailIngredient;

import java.util.List;

public interface CocktailIngredientDao {
    void saveIngredients(long cocktailId, List<CocktailIngredient> ingredients) throws DaoException;
}
