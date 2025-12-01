package com.filippovich.webtask.service;

import com.filippovich.webtask.dao.UserDao;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;

import java.time.LocalDateTime;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> registerUser(String username, String email, String password) {
        Optional<User> existingUser = userDao.findByEmail(email);
        if (existingUser.isPresent()) {
            return Optional.empty(); // Email уже используется
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashPassword(password));
        newUser.setRole(UserRole.CLIENT);
        newUser.setCreatedAt(LocalDateTime.now());

        boolean saved = userDao.save(newUser);
        if (!saved) {
            return Optional.empty(); // Не удалось сохранить
        }

        return Optional.of(newUser);
    }


    public Optional<User> loginUser(String email, String password) {
        String hashedPassword = hashPassword(password);
        return userDao.authentication(email, hashedPassword);
    }

    private String hashPassword(String password) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        return passwordEncoder.hash(password);
    }
}
