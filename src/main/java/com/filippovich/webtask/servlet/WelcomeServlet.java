package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.DatabaseConfig;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        req.setAttribute("currentUser", currentUser);

        DataSource ds = DatabaseConfig.getDataSource();
        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ds);

        List<Cocktail> cocktails;
        try {
            cocktails = cocktailService.getCocktailsByStatus("APPROVED");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("cocktailList", cocktails);
        req.getRequestDispatcher("WEB-INF/pages/welcome.jsp").forward(req, resp);
    }
}
