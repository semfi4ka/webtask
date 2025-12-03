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

@WebServlet("/approve")
public class CocktailApproveServlet extends HttpServlet {

    private CocktailServiceImpl cocktailService;

    @Override
    public void init() {
        DataSource ds = DatabaseConfig.getDataSource();
        cocktailService = new CocktailServiceImpl(ds);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        if (currentUser == null ||
                (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.BARTENDER)) {
            resp.sendRedirect(req.getContextPath() + "/welcome");
            return;
        }

        try {
            List<Cocktail> pendingCocktails = cocktailService.getCocktailsByStatus("MODERATION");
            req.setAttribute("pendingCocktails", pendingCocktails);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        req.getRequestDispatcher("/WEB-INF/pages/approve.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        if (currentUser == null ||
                (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.BARTENDER)) {
            resp.sendRedirect(req.getContextPath() + "/welcome");
            return;
        }

        String action = req.getParameter("action"); // approve or reject
        long cocktailId = Long.parseLong(req.getParameter("cocktailId"));

        try {
            switch (action) {
                case "approve":
                    cocktailService.getCocktailById(cocktailId).ifPresent(cocktail -> {
                        try {
                            cocktail.setStatus(com.filippovich.webtask.model.CocktailStatus.APPROVED);
                            cocktailService.updateCocktail(cocktail);
                        } catch (com.filippovich.webtask.exception.DaoException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                case "reject":
                    cocktailService.deleteCocktail(cocktailId);
                    break;
            }
        } catch (com.filippovich.webtask.exception.DaoException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/approve");
    }

}
