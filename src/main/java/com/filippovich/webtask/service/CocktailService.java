package com.filippovich.webtask.service;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;

import java.util.List;
import java.util.Optional;

public interface CocktailService {
    List<Cocktail> getAllCocktails() throws DaoException;

    List<Cocktail> getCocktailsByStatus(String status) throws DaoException;

    Optional<Cocktail> getCocktailById(long id) throws DaoException;

    boolean addCocktail(Cocktail cocktail) throws DaoException;

    boolean updateCocktail(Cocktail cocktail) throws DaoException;

    boolean deleteCocktail(long id);
}
