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

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/view")
public class CocktailViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String cocktailIdParam = req.getParameter("id");
        if (cocktailIdParam == null) {
            resp.sendRedirect(req.getContextPath() + "/welcome");
            return;
        }

        long cocktailId;
        try {
            cocktailId = Long.parseLong(cocktailIdParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/welcome");
            return;
        }

        DataSource ds = DatabaseConfig.getDataSource();
        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ds);

        try {
            Optional<Cocktail> optionalCocktail = cocktailService.getCocktailById(cocktailId);
            if (optionalCocktail.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/welcome");
                return;
            }

            Cocktail cocktail = optionalCocktail.get();
            String authorName = cocktailService.getAuthorNameById(cocktail.getAuthor().getId());
            List<String> ingredients = cocktailService.getIngredientsByCocktailId(cocktail.getId());

            req.setAttribute("cocktail", cocktail);
            req.setAttribute("authorName", authorName);
            req.setAttribute("ingredients", ingredients);
            req.setAttribute("currentUser", currentUser);

            req.getRequestDispatcher("WEB-INF/pages/cocktail_view.jsp").forward(req, resp);

        } catch (DaoException e) {
            throw new ServletException("Error retrieving cocktail data", e);
        }
    }
}
