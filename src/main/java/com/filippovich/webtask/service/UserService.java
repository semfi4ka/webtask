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

    public String registerUser(String username, String email, String password) {
        Optional<User> existingUser = userDao.findByEmail(email);
        if (existingUser.isPresent()) {
            return "Error: Email address already in use";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashPassword(password));
        newUser.setRole(UserRole.CLIENT);
        newUser.setCreatedAt(LocalDateTime.now());

        boolean saved = userDao.save(newUser);
        if (!saved) {
            return "Registration error: Failed to save user";
        }

        return "Registration successful!";
    }

    public String loginUser(String email, String password) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        String hashedPassword = passwordEncoder.hash(password);
        Optional<User> userOpt = userDao.authentication(email, hashedPassword);
        if (userOpt.isPresent()) {
            return "SUCCESS:" + userOpt.get().getUsername();
        } else {
            return "Invalid email or password";
        }
    }
    private String hashPassword(String password) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        return passwordEncoder.hash(password);
    }
}
