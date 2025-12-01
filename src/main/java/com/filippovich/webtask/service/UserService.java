package com.filippovich.webtask.service;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.util.PasswordEncoder;

import java.util.Optional;

public interface UserService {
    Optional<User> registerUser(String username, String email, String password) throws DaoException;

    Optional<User> loginUser(String email, String password) throws DaoException;

    default String hashPassword(String password) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        return passwordEncoder.hash(password);
    }
}
