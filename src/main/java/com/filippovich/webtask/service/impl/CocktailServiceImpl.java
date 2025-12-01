package com.filippovich.webtask.service.impl;

import com.filippovich.webtask.dao.impl.CocktailDaoImpl;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.service.CocktailService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class CocktailServiceImpl implements CocktailService {

    private final CocktailDaoImpl cocktailDao;

    public CocktailServiceImpl(DataSource dataSource) {   // dao сделаю singleton
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
    public boolean addCocktail(Cocktail cocktail) throws DaoException {
        return cocktailDao.save(cocktail);
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
}
