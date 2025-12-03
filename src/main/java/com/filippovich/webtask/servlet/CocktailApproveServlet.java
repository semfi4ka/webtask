package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.DatabaseConfig;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailStatus;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(CocktailApproveServlet.URL_MAPPING)
public class CocktailApproveServlet extends HttpServlet {

    public static final String URL_MAPPING = "/approve";
    public static final String PAGE_APPROVE = "/WEB-INF/pages/approve.jsp";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_PENDING_COCKTAILS = "pendingCocktails";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_COCKTAIL_ID = "cocktailId";
    public static final String PAGE_WELCOME = "/welcome";

    private CocktailServiceImpl cocktailService;

    @Override
    public void init() {
        cocktailService = new CocktailServiceImpl(DatabaseConfig.getDataSource());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null ||
                (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.BARTENDER)) {
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        try {
            List<Cocktail> pendingCocktails = cocktailService.getCocktailsByStatus("MODERATION");
            req.setAttribute(ATTR_PENDING_COCKTAILS, pendingCocktails);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        req.getRequestDispatcher(PAGE_APPROVE).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null ||
                (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.BARTENDER)) {
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        String action = req.getParameter(PARAM_ACTION);
        long cocktailId = Long.parseLong(req.getParameter(PARAM_COCKTAIL_ID));

        try {
            switch (action) {
                case "approve":
                    cocktailService.getCocktailById(cocktailId).ifPresent(cocktail -> {
                        try {
                            cocktail.setStatus(CocktailStatus.APPROVED);
                            cocktailService.updateCocktail(cocktail);
                        } catch (DaoException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                case "reject":
                    cocktailService.deleteCocktail(cocktailId);
                    break;
            }
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + URL_MAPPING);
    }
}
