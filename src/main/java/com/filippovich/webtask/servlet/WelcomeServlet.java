package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.DatabaseConfig;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.CocktailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.filippovich.webtask.model.UserRole;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        if (currentUser == null) {
            resp.sendRedirect("login");
            return;
        }

        req.setAttribute("username", currentUser.getUsername());
        req.setAttribute("role", currentUser.getRole().name()); // CLIENT, BARTENDER, ADMIN

        if (currentUser.getRole() == UserRole.CLIENT) {
            DataSource ds = DatabaseConfig.getDataSource();
            CocktailService cocktailService = new CocktailService(ds);
            List<Cocktail> cocktails = cocktailService.getAllCocktails();
            req.setAttribute("cocktails", cocktails);
        }

        req.getRequestDispatcher("WEB-INF/pages/welcome.jsp").forward(req, resp);
    }
}

