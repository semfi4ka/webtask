package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.DatabaseConfig;
import com.filippovich.webtask.dao.impl.CocktailDaoImpl;
import com.filippovich.webtask.dao.impl.CocktailIngredientDaoImpl;
import com.filippovich.webtask.dao.impl.IngredientDaoImpl;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;
import com.filippovich.webtask.model.CocktailStatus;
import com.filippovich.webtask.model.Ingredient;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
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
import java.util.Optional;

@WebServlet("/cocktail/add")
public class CocktailAddServlet extends HttpServlet {

    private CocktailDaoImpl cocktailDao;
    private IngredientDaoImpl ingredientDao;
    private CocktailIngredientDaoImpl cocktailIngredientDao;

    @Override
    public void init() {
        DataSource ds = DatabaseConfig.getDataSource();
        cocktailDao = new CocktailDaoImpl(ds);
        ingredientDao = new IngredientDaoImpl(ds);
        cocktailIngredientDao = new CocktailIngredientDaoImpl(ds);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect("../login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/pages/add.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect("../login");
            return;
        }

        String name = req.getParameter("name");
        String description = req.getParameter("description");

        Cocktail cocktail = new Cocktail();
        cocktail.setName(name);
        cocktail.setDescription(description);
        cocktail.setAuthor(user);
        cocktail.setCreatedAt(LocalDateTime.now());

        if (user.getRole() == UserRole.CLIENT) {
            cocktail.setStatus(CocktailStatus.MODERATION);
        } else {
            cocktail.setStatus(CocktailStatus.APPROVED);
        }

        try {
            boolean saved = cocktailDao.save(cocktail);

            if (saved) {
                // обработка ингредиентов
                String[] ingredientNames = req.getParameterValues("ingredientName");
                String[] ingredientAmounts = req.getParameterValues("ingredientAmount");
                String[] ingredientUnits = req.getParameterValues("ingredientUnit");

                List<CocktailIngredient> ingredientsList = new ArrayList<>();

                if (ingredientNames != null) {
                    for (int i = 0; i < ingredientNames.length; i++) {
                        String inName = ingredientNames[i].trim();
                        String amountStr = ingredientAmounts[i].trim();
                        String unit = ingredientUnits[i].trim();

                        if (inName.isEmpty() || amountStr.isEmpty() || unit.isEmpty()) continue;

                        double amount;
                        try {
                            amount = Double.parseDouble(amountStr);
                        } catch (NumberFormatException e) {
                            continue; // пропускаем некорректные значения
                        }

                        // ищем ингредиент в базе
                        Optional<Ingredient> existingIngredient = ingredientDao.findByName(inName);
                        Ingredient ingredient;
                        if (existingIngredient.isPresent()) {
                            ingredient = existingIngredient.get();
                        } else {
                            ingredient = new Ingredient();
                            ingredient.setName(inName);
                            ingredient.setUnit(unit);
                            ingredientDao.save(ingredient);
                        }

                        CocktailIngredient ci = new CocktailIngredient();
                        ci.setIngredient(ingredient);
                        ci.setAmount(amount);
                        ingredientsList.add(ci);
                    }

                    if (!ingredientsList.isEmpty()) {
                        cocktailIngredientDao.saveIngredients(cocktail.getId(), ingredientsList);
                    }
                }

                req.setAttribute("success", true);
                req.setAttribute("message", "Cocktail added successfully!");
            } else {
                req.setAttribute("success", false);
                req.setAttribute("message", "Failed to add cocktail!");
            }

        } catch (DaoException e) {
            throw new ServletException(e);
        }

        req.getRequestDispatcher("/WEB-INF/pages/add.jsp").forward(req, resp);
    }
}
