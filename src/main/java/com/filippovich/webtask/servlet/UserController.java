package com.filippovich.webtask.servlet;

import com.filippovich.webtask.dao.impl.UserDaoImpl;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet({"/register", "/login"})
public class UserController extends HttpServlet {

    public static final String LOGIN_PAGE = "WEB-INF/pages/login.jsp";
    public static final String REGISTER_PAGE = "WEB-INF/pages/register.jsp";
    public static final String LOGIN_SERVLET = "/login";
    public static final String REGISTER_SERVLET = "/register";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String WELCOME_PAGE = "welcome";
    private UserServiceImpl userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl(new UserDaoImpl());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (LOGIN_SERVLET.equals(path)) {
            req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
        } else {
            req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            if (REGISTER_SERVLET.equals(path)) {
                handleRegister(req, resp);
            } else if (LOGIN_SERVLET.equals(path)) {
                handleLogin(req, resp);
            }
        }
            catch (Exception e) {
            throw new ServletException(e);
            }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws DaoException {
        String username = req.getParameter(USERNAME);
        String email = req.getParameter(EMAIL);
        String password = req.getParameter(PASSWORD);
        try {
            Optional<User> registeredUser = userService.registerUser(username, email, password);
            if (registeredUser.isPresent()) {
                req.setAttribute("message", "Registration successful!");
            } else {
                req.setAttribute("message", "Error: Email is already in use or registration failed");
            }
            req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
        }
       catch (Exception e) {
            throw new DaoException(e);
       }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, DaoException {
        String email = req.getParameter(EMAIL);
        String password = req.getParameter(PASSWORD);

        Optional<User> userOpt = userService.loginUser(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            req.getSession().setAttribute("currentUser", user);
            resp.sendRedirect(WELCOME_PAGE);
        } else {
            req.setAttribute("message", "Invalid email or password");
            req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
        }
    }
}
