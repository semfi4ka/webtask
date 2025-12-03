package com.filippovich.webtask.service;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;

import java.util.List;
import java.util.Optional;

public interface CocktailService {
    List<Cocktail> getAllCocktails() throws DaoException;

    List<Cocktail> getCocktailsByStatus(String status) throws DaoException;

    Optional<Cocktail> getCocktailById(long id) throws DaoException;

    boolean updateCocktail(Cocktail cocktail) throws DaoException;

    boolean deleteCocktail(long id);

    String getAuthorNameById(long authorId) throws DaoException;

    List<String> getIngredientsByCocktailId(long cocktailId) throws DaoException;

    boolean addCocktailWithIngredients(Cocktail cocktail, List<CocktailIngredient> ingredients, String role) throws DaoException;
}
