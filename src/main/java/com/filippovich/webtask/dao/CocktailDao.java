package com.filippovich.webtask.dao;

import com.filippovich.webtask.model.Cocktail;
import java.util.List;
import java.util.Optional;

public interface CocktailDao {
    Optional<Cocktail> findById(long id);
    List<Cocktail> findAll();
    boolean save(Cocktail cocktail);
    boolean update(Cocktail cocktail);
    boolean delete(long id);
    List<Cocktail> findByStatus(String status);
}
