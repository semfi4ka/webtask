package com.filippovich.webtask.dao;

import com.filippovich.webtask.model.Ingredient;
import java.util.List;
import java.util.Optional;

public interface IngredientDao {
    Optional<Ingredient> findById(long id);
    Optional<Ingredient> findByName(String name);
    List<Ingredient> findAll();
    boolean save(Ingredient ingredient);
    boolean update(Ingredient ingredient);
    boolean delete(long id);
}
