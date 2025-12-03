package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.DatabaseConfig;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(WelcomeServlet.URL_MAPPING)
public class WelcomeServlet extends HttpServlet {

    public static final String URL_MAPPING = "/welcome";
    public static final String PAGE_WELCOME = "WEB-INF/pages/welcome.jsp";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_COCKTAIL_LIST = "cocktailList";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            resp.sendRedirect("login");
            return;
        }

        req.setAttribute(ATTR_CURRENT_USER, currentUser);

        CocktailServiceImpl cocktailService = new CocktailServiceImpl(DatabaseConfig.getDataSource());
        List<Cocktail> cocktails;
        try {
            cocktails = cocktailService.getCocktailsByStatus("APPROVED");
        } catch (DaoException e) {
            throw new ServletException(e);
        }

        req.setAttribute(ATTR_COCKTAIL_LIST, cocktails);
        req.getRequestDispatcher(PAGE_WELCOME).forward(req, resp);
    }
}
