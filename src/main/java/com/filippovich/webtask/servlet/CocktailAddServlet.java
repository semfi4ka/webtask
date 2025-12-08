package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;
import com.filippovich.webtask.model.Ingredient;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(CocktailAddServlet.URL_MAPPING)
public class CocktailAddServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CocktailAddServlet.class);

    public static final String URL_MAPPING = "/add";
    public static final String PAGE_ADD = "/WEB-INF/pages/add.jsp";
    public static final String PAGE_WELCOME = "/welcome";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_INGREDIENT_NAME = "ingredientName";
    public static final String PARAM_INGREDIENT_AMOUNT = "ingredientAmount";
    public static final String PARAM_INGREDIENT_UNIT = "ingredientUnit";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String LOGIN_PATH = "/login";

    private CocktailServiceImpl cocktailService;
    private DataSource dataSource;

    @Override
    public void init() {
        logger.info("Initializing CocktailAddServlet...");
        dataSource = ConnectionDataSource.getDataSource();
        cocktailService = new CocktailServiceImpl(dataSource);
        logger.info("CocktailAddServlet initialized successfully.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            logger.warn("Unauthorized access attempt to /add. Redirecting to login.");
            resp.sendRedirect(req.getContextPath() + LOGIN_PATH);
            return;
        }
        logger.info("User '{}' accessed the add cocktail page.", currentUser.getEmail());
        req.setAttribute(ATTR_CURRENT_USER, currentUser);
        req.getRequestDispatcher(PAGE_ADD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            logger.warn("Unauthorized POST attempt to /add. Redirecting to login.");
            resp.sendRedirect(req.getContextPath() + LOGIN_PATH);
            return;
        }

        String name = req.getParameter(PARAM_NAME);
        String description = req.getParameter(PARAM_DESCRIPTION);
        logger.info("User '{}' is adding a new cocktail: '{}'", currentUser.getEmail(), name);

        String[] ingredientNames = req.getParameterValues(PARAM_INGREDIENT_NAME);
        String[] ingredientAmounts = req.getParameterValues(PARAM_INGREDIENT_AMOUNT);
        String[] ingredientUnits = req.getParameterValues(PARAM_INGREDIENT_UNIT);

        Cocktail cocktail = new Cocktail();
        cocktail.setName(name);
        cocktail.setDescription(description);
        cocktail.setAuthor(currentUser);
        cocktail.setCreatedAt(java.time.LocalDateTime.now());

        List<CocktailIngredient> ingredientList = new ArrayList<>();
        if (ingredientNames != null) {
            for (int i = 0; i < ingredientNames.length; i++) {
                String ingredient_name = ingredientNames[i].trim();
                String ingredient_amount = ingredientAmounts[i].trim();
                String ingredient_unit = ingredientUnits[i].trim();

                if (ingredient_name.isEmpty() && ingredient_amount.isEmpty() && ingredient_unit.isEmpty()) continue;

                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredient_name);
                ingredient.setUnit(ingredient_unit);

                CocktailIngredient cocktailIngredient = new CocktailIngredient();
                cocktailIngredient.setIngredient(ingredient);
                cocktailIngredient.setAmount(ingredient_amount.isEmpty() ? 0 : Double.parseDouble(ingredient_amount));

                ingredientList.add(cocktailIngredient);
            }
        }

        try {
            boolean saved = cocktailService.addCocktailWithIngredients(cocktail, ingredientList, currentUser.getRole().name());
            if (saved) {
                logger.info("Cocktail '{}' successfully added by user '{}'.", cocktail.getName(), currentUser.getEmail());
            } else {
                logger.warn("Cocktail '{}' could not be saved for user '{}'.", cocktail.getName(), currentUser.getEmail());
            }
        } catch (ServiceException e) {
            logger.error("Error adding cocktail '{}' by user '{}'.", cocktail.getName(), currentUser.getEmail(), e);
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
    }
}
