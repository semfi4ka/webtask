package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(CocktailViewServlet.URL_MAPPING)
public class CocktailViewServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CocktailViewServlet.class);

    public static final String URL_MAPPING = "/view";
    public static final String PAGE_VIEW = "/WEB-INF/pages/cocktail_view.jsp";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_COCKTAIL = "cocktail";
    public static final String ATTR_AUTHOR_NAME = "authorName";
    public static final String ATTR_INGREDIENTS = "ingredients";
    public static final String PAGE_WELCOME = "/welcome";
    public static final String LOGIN_URL = "/login";
    public static final String ID_PARAMETER = "id";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            logger.warn("Unauthorized access attempt to view cocktail. Redirecting to login.");
            resp.sendRedirect(req.getContextPath() + LOGIN_URL);
            return;
        }

        String cocktailIdParam = req.getParameter(ID_PARAMETER);
        if (cocktailIdParam == null) {
            logger.warn("No cocktail ID provided by user '{}'. Redirecting to welcome page.", currentUser.getEmail());
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        long cocktailId;
        try {
            cocktailId = Long.parseLong(cocktailIdParam);
        } catch (NumberFormatException e) {
            logger.warn("Invalid cocktail ID '{}' provided by user '{}'. Redirecting to welcome page.", cocktailIdParam, currentUser.getEmail());
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ConnectionDataSource.getDataSource());

        try {
            var optionalCocktail = cocktailService.getCocktailById(cocktailId);
            if (optionalCocktail.isEmpty()) {
                logger.warn("Cocktail with ID '{}' not found for user '{}'. Redirecting to welcome page.", cocktailId, currentUser.getEmail());
                resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
                return;
            }

            Cocktail cocktail = optionalCocktail.get();
            String authorName = cocktailService.getAuthorNameById(cocktail.getAuthor().getId());
            List<String> ingredients = cocktailService.getIngredientsByCocktailId(cocktail.getId());

            req.setAttribute(ATTR_COCKTAIL, cocktail);
            req.setAttribute(ATTR_AUTHOR_NAME, authorName);
            req.setAttribute(ATTR_INGREDIENTS, ingredients);
            req.setAttribute(ATTR_CURRENT_USER, currentUser);

            logger.info("User '{}' is viewing cocktail '{}' (ID {}).", currentUser.getEmail(), cocktail.getName(), cocktail.getId());
            req.getRequestDispatcher(PAGE_VIEW).forward(req, resp);

        } catch (ServiceException e) {
            logger.error("Error retrieving cocktail data for cocktail ID '{}' by user '{}'.", cocktailId, currentUser.getEmail(), e);
            throw new ServletException("Error retrieving cocktail data", e);
        }
    }
}
