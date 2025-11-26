package com.filippovich.webtask.dao;

import com.filippovich.webtask.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean save(User user);
    boolean update(User user);
    boolean delete(long id);
    Optional<User> authentication(String email, String passwordHash);
}
