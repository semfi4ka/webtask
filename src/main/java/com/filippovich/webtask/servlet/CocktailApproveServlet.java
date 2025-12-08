package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(CocktailApproveServlet.URL_MAPPING)
public class CocktailApproveServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CocktailApproveServlet.class);

    public static final String URL_MAPPING = "/approve";
    public static final String PAGE_APPROVE = "/WEB-INF/pages/approve.jsp";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_PENDING_COCKTAILS = "pendingCocktails";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_COCKTAIL_ID = "cocktailId";
    public static final String PAGE_WELCOME = "/welcome";
    public static final String MODERATION_STATUS = "MODERATION";
    public static final String APPROVE_CASE = "approve";
    public static final String REJECT_CASE = "reject";

    private CocktailServiceImpl cocktailService;

    @Override
    public void init() {
        logger.info("Initializing CocktailApproveServlet...");
        cocktailService = new CocktailServiceImpl(ConnectionDataSource.getDataSource());
        logger.info("CocktailApproveServlet initialized successfully.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null ||
                (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.BARTENDER)) {
            logger.warn("Unauthorized access attempt to /approve by user '{}'", currentUser != null ? currentUser.getEmail() : "anonymous");
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        try {
            List<Cocktail> pendingCocktails = cocktailService.getCocktailsByStatus(MODERATION_STATUS);
            req.setAttribute(ATTR_PENDING_COCKTAILS, pendingCocktails);
            logger.info("User '{}' accessed the approve page. Pending cocktails count: {}", currentUser.getEmail(), pendingCocktails.size());
        } catch (ServiceException e) {
            logger.error("Error fetching pending cocktails for user '{}'", currentUser.getEmail(), e);
            throw new ServletException(e);
        }

        req.getRequestDispatcher(PAGE_APPROVE).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null ||
                (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.BARTENDER)) {
            logger.warn("Unauthorized POST attempt to /approve by user '{}'", currentUser != null ? currentUser.getEmail() : "anonymous");
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        String action = req.getParameter(PARAM_ACTION);
        long cocktailId = Long.parseLong(req.getParameter(PARAM_COCKTAIL_ID));

        logger.info("User '{}' performing '{}' action on cocktail with ID {}", currentUser.getEmail(), action, cocktailId);

        try {
            switch (action) {
                case APPROVE_CASE:
                    cocktailService.getCocktailById(cocktailId).ifPresent(cocktail -> {
                        try {
                            cocktail.setStatus(CocktailStatus.APPROVED);
                            cocktailService.updateCocktail(cocktail);
                            logger.info("Cocktail '{}' (ID {}) approved by user '{}'", cocktail.getName(), cocktail.getId(), currentUser.getEmail());
                        } catch (ServiceException e) {
                            logger.error("Error approving cocktail ID {} by user '{}'", cocktailId, currentUser.getEmail(), e);
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                case REJECT_CASE:
                    cocktailService.deleteCocktail(cocktailId);
                    logger.info("Cocktail with ID {} rejected (deleted) by user '{}'", cocktailId, currentUser.getEmail());
                    break;
                default:
                    logger.warn("Unknown action '{}' attempted by user '{}'", action, currentUser.getEmail());
            }
        } catch (ServiceException e) {
            logger.error("ServiceException when processing action '{}' on cocktail ID {} by user '{}'", action, cocktailId, currentUser.getEmail(), e);
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + URL_MAPPING);
    }
}
