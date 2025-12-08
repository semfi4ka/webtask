package com.filippovich.webtask.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(LogoutServlet.class);

    public static final String LOGIN_REDIRECT = "login";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Object userObj = req.getSession().getAttribute("currentUser");
        if (userObj != null) {
            logger.info("User '{}' logged out.", userObj);
        } else {
            logger.warn("Logout attempted with no user in session.");
        }

        req.getSession().invalidate();
        resp.sendRedirect(LOGIN_REDIRECT);
    }
}
