package com.filippovich.webtask.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    private static final Logger logger = LogManager.getLogger(PasswordEncoder.class);
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

            logger.debug("Password successfully hashed");
            return stringBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            logger.fatal("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
