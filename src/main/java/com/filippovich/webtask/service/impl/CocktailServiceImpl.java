package com.filippovich.webtask.service.impl;

import com.filippovich.webtask.dao.impl.CocktailDaoImpl;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;
import com.filippovich.webtask.model.CocktailStatus;
import com.filippovich.webtask.service.CocktailService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class CocktailServiceImpl implements CocktailService {

    private final CocktailDaoImpl cocktailDao;

    public CocktailServiceImpl(DataSource dataSource) {
        this.cocktailDao = new CocktailDaoImpl(dataSource);
    }

    @Override
    public List<Cocktail> getAllCocktails() throws DaoException {
        return cocktailDao.findAll();
    }

    @Override
    public List<Cocktail> getCocktailsByStatus(String status) throws DaoException {
        return cocktailDao.findByStatus(status);
    }

    @Override
    public Optional<Cocktail> getCocktailById(long id) throws DaoException {
        return cocktailDao.findById(id);
    }

    @Override
    public boolean updateCocktail(Cocktail cocktail) throws DaoException {
        return cocktailDao.update(cocktail);
    }

    @Override
    public boolean deleteCocktail(long id) {
        try {
            return cocktailDao.delete(id);
        } catch (com.filippovich.webtask.exception.DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAuthorNameById(long authorId) throws DaoException {
        return cocktailDao.findAuthorNameById(authorId);
    }

    @Override
    public List<String> getIngredientsByCocktailId(long cocktailId) throws DaoException {
        return cocktailDao.findIngredientsByCocktailId(cocktailId);
    }

    @Override
    public boolean addCocktailWithIngredients(Cocktail cocktail, List<CocktailIngredient> ingredients, String role) throws DaoException {
        switch (role) {
            case "CLIENT":
                cocktail.setStatus(CocktailStatus.MODERATION);
                break;
            case "BARTENDER":
            case "ADMIN":
                cocktail.setStatus(CocktailStatus.APPROVED);
                break;
            default:
                cocktail.setStatus(CocktailStatus.DRAFT);
        }
        return cocktailDao.saveCocktailWithIngredients(cocktail, ingredients);
    }



}
