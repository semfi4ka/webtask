package com.filippovich.webtask.dao;

import com.filippovich.webtask.model.Cocktail;
import java.util.List;
import java.util.Optional;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.CocktailIngredient;

public interface CocktailDao {
    Optional<Cocktail> findById(long id) throws DaoException;
    List<Cocktail> findAll()  throws DaoException;
    boolean save(Cocktail cocktail)  throws DaoException;
    boolean update(Cocktail cocktail) throws DaoException;
    boolean delete(long id) throws DaoException;
    List<String> findIngredientsByCocktailId(long cocktailId) throws DaoException;
    String findAuthorNameById(long authorId) throws DaoException;
    List<Cocktail> findByStatus(String status) throws DaoException;

    boolean saveCocktailWithIngredients(Cocktail cocktail, List<CocktailIngredient> ingredients) throws DaoException;
}
