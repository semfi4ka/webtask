package com.filippovich.webtask.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    private static final String FORMAT = "%02x";
    private static final String ALGORITHM = "SHA-256";
    public String hash(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashed = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashed) {
                stringBuilder.append(String.format(FORMAT, b));
            }

            return stringBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e); // заменю на логи!!!
        }
    }
}
