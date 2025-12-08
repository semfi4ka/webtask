package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(DeleteCocktailServlet.URL_MAPPING)
public class DeleteCocktailServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DeleteCocktailServlet.class);

    public static final String URL_MAPPING = "/delete";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String PAGE_WELCOME = "/welcome";
    public static final String PARAM_COCKTAIL_ID = "id";
    public static final String LOGIN_URL = "/login";

    private CocktailServiceImpl cocktailService;
    private DataSource dataSource;

    @Override
    public void init() {
        dataSource = ConnectionDataSource.getDataSource();
        cocktailService = new CocktailServiceImpl(dataSource);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            logger.warn("Unauthorized attempt to delete cocktail.");
            resp.sendRedirect(req.getContextPath() + LOGIN_URL);
            return;
        }

        if (currentUser.getRole() != UserRole.ADMIN) {
            logger.warn("User '{}' attempted to delete cocktail without ADMIN role.", currentUser.getEmail());
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        String idParam = req.getParameter(PARAM_COCKTAIL_ID);
        if (idParam == null) {
            logger.warn("User '{}' attempted to delete a cocktail without providing an ID.", currentUser.getEmail());
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            logger.warn("User '{}' provided invalid cocktail ID '{}'.", currentUser.getEmail(), idParam);
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        try {
            cocktailService.deleteCocktail(id);
            logger.info("User '{}' successfully deleted cocktail with ID {}.", currentUser.getEmail(), id);
        } catch (ServiceException e) {
            logger.error("Error deleting cocktail with ID {} by user '{}'.", id, currentUser.getEmail(), e);
            throw new ServletException("Error deleting cocktail", e);
        }

        resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
    }
}
