package com.filippovich.webtask.service.impl;

import com.filippovich.webtask.dao.UserDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
import com.filippovich.webtask.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> registerUser(String username, String email, String password) throws DaoException {
        Optional<User> existingUser = userDao.findByEmail(email);
        if (existingUser.isPresent()) {
            return Optional.empty();
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashPassword(password));
        newUser.setRole(UserRole.CLIENT);
        newUser.setCreatedAt(LocalDateTime.now());

        try {
            boolean saved = userDao.save(newUser);
            if (!saved) {
                return Optional.empty();
            }
        }
        catch (Exception e) {
            throw new DaoException(e);
        }

        return Optional.of(newUser);
    }


    @Override
    public Optional<User> loginUser(String email, String password) throws DaoException {
        String hashedPassword = hashPassword(password);
        return userDao.authentication(email, hashedPassword);
    }

}
