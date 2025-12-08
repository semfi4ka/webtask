package com.filippovich.webtask.service;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;

import java.util.List;
import java.util.Optional;

public interface CocktailService {
    List<Cocktail> getAllCocktails() throws ServiceException;

    List<Cocktail> getCocktailsByStatus(String status) throws ServiceException;

    Optional<Cocktail> getCocktailById(long id) throws ServiceException;

    boolean updateCocktail(Cocktail cocktail) throws ServiceException;

    boolean deleteCocktail(long id) throws ServiceException;

    String getAuthorNameById(long authorId) throws ServiceException;

    List<String> getIngredientsByCocktailId(long cocktailId) throws ServiceException;

    boolean addCocktailWithIngredients(Cocktail cocktail, List<CocktailIngredient> ingredients, String role) throws ServiceException;
}
