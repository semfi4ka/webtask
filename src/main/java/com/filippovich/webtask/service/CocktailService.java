package com.filippovich.webtask.service;

import com.filippovich.webtask.dao.impl.CocktailDaoImpl;
import com.filippovich.webtask.model.Cocktail;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class CocktailService {

    private final CocktailDaoImpl cocktailDao;

    public CocktailService(DataSource dataSource) {
        this.cocktailDao = new CocktailDaoImpl(dataSource);
    }

    public List<Cocktail> getAllCocktails() {
        return cocktailDao.findAll();
    }

    public List<Cocktail> getCocktailsByStatus(String status) {
        return cocktailDao.findByStatus(status);
    }

    public Optional<Cocktail> getCocktailById(long id) {
        return cocktailDao.findById(id);
    }

    public boolean addCocktail(Cocktail cocktail) {
        return cocktailDao.save(cocktail);
    }

    public boolean updateCocktail(Cocktail cocktail) {
        return cocktailDao.update(cocktail);
    }

    public boolean deleteCocktail(long id) {
        return cocktailDao.delete(id);
    }
}
