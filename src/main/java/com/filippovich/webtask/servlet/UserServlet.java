package com.filippovich.webtask.servlet;

import com.filippovich.webtask.dao.impl.UserDaoImpl;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet({"/register", "/login"})
public class UserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        // Инициализация UserService с DAO
        userService = new UserService(new UserDaoImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/login".equals(path)) {
            req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("WEB-INF/pages/register.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/login".equals(path)) {
            handleLogin(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Optional<User> registeredUser = userService.registerUser(username, email, password);
        if (registeredUser.isPresent()) {
            req.setAttribute("message", "Регистрация успешна!");
        } else {
            req.setAttribute("message", "Ошибка: Email уже занят или регистрация не удалась");
        }
        req.getRequestDispatcher("WEB-INF/pages/register.jsp").forward(req, resp);
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Optional<User> userOpt = userService.loginUser(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            req.getSession().setAttribute("currentUser", user);
            resp.sendRedirect("welcome");
        } else {
            req.setAttribute("message", "Неверный email или пароль");
            req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
        }
    }
}
