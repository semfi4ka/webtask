package com.filippovich.webtask.dao;

import com.filippovich.webtask.model.Ingredient;
import java.util.List;
import java.util.Optional;
import com.filippovich.webtask.exception.DaoException;

public interface IngredientDao {
    Optional<Ingredient> findById(long id)  throws DaoException;
    Optional<Ingredient> findByName(String name)   throws DaoException;
    List<Ingredient> findAll() throws  DaoException;
    boolean save(Ingredient ingredient) throws DaoException;
    boolean update(Ingredient ingredient)  throws DaoException;
    boolean delete(long id)  throws DaoException;
}
