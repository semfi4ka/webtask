package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.DatabaseConfig;
import com.filippovich.webtask.exception.DaoException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(CocktailAddServlet.URL_MAPPING)
public class CocktailAddServlet extends HttpServlet {

    public static final String URL_MAPPING = "/add";
    public static final String PAGE_ADD = "/WEB-INF/pages/add.jsp";
    public static final String PAGE_WELCOME = "/welcome";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_INGREDIENT_NAME = "ingredientName";
    public static final String PARAM_INGREDIENT_AMOUNT = "ingredientAmount";
    public static final String PARAM_INGREDIENT_UNIT = "ingredientUnit";
    public static final String ATTR_CURRENT_USER = "currentUser";

    private CocktailServiceImpl cocktailService;
    private DataSource dataSource;

    @Override
    public void init() {
        dataSource = DatabaseConfig.getDataSource();
        cocktailService = new CocktailServiceImpl(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute(ATTR_CURRENT_USER, currentUser);
        req.getRequestDispatcher(PAGE_ADD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String name = req.getParameter(PARAM_NAME);
        String description = req.getParameter(PARAM_DESCRIPTION);

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
                String iname = ingredientNames[i].trim();
                String iamount = ingredientAmounts[i].trim();
                String iunit = ingredientUnits[i].trim();

                if (iname.isEmpty() && iamount.isEmpty() && iunit.isEmpty()) continue;

                Ingredient ingredient = new Ingredient();
                ingredient.setName(iname);
                ingredient.setUnit(iunit);

                CocktailIngredient cocktailIngredient = new CocktailIngredient();
                cocktailIngredient.setIngredient(ingredient);
                cocktailIngredient.setAmount(iamount.isEmpty() ? 0 : Double.parseDouble(iamount));

                ingredientList.add(cocktailIngredient);
            }
        }

        try {
            cocktailService.addCocktailWithIngredients(cocktail, ingredientList, currentUser.getRole().name());
        } catch (DaoException e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
    }
}
