package com.filippovich.webtask.servlet;

import com.filippovich.webtask.dao.impl.UserDaoImpl;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
import com.filippovich.webtask.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class UserManagementServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(UserManagementServlet.class);

    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_USER_LIST = "userList";
    public static final String PAGE_USER_MANAGEMENT = "/WEB-INF/pages/user_management.jsp";
    public static final String WELCOME_URL = "/welcome";
    public static final String ACTION_PARAMETER = "action";
    public static final String USER_ID_PARAMETER = "userId";
    public static final String PROMOTE_CASE = "promote";
    public static final String DEMOTE_CASE = "demote";
    public static final String PATH = "/admin/users";

    private UserServiceImpl userService;

    @Override
    public void init() {
        userService = new UserServiceImpl(new UserDaoImpl());
        logger.info("UserManagementServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);

        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            logger.warn("Unauthorized access attempt to UserManagementServlet by user: {}", currentUser);
            resp.sendRedirect(req.getContextPath() + WELCOME_URL);
            return;
        }

        try {
            List<User> users = userService.getAllUsers();
            for (User u : users) {
                int count = userService.getCocktailCountByUser(u);
                u.setCocktailCount(count);
            }
            req.setAttribute(ATTR_USER_LIST, users);
            req.getRequestDispatcher(PAGE_USER_MANAGEMENT).forward(req, resp);
            logger.info("Admin '{}' viewed user management page.", currentUser.getUsername());
        } catch (ServiceException e) {
            logger.error("Error retrieving user list for admin '{}'", currentUser.getUsername(), e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);

        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            logger.warn("Unauthorized POST attempt to UserManagementServlet by user: {}", currentUser);
            resp.sendRedirect(req.getContextPath() + WELCOME_URL);
            return;
        }

        String action = req.getParameter(ACTION_PARAMETER);
        long userId = Long.parseLong(req.getParameter(USER_ID_PARAMETER));

        try {
            User user = userService.getUserById(userId);
            if (user != null && user.getRole() != UserRole.ADMIN) {
                switch (action) {
                    case PROMOTE_CASE:
                        if (user.getRole() == UserRole.CLIENT) {
                            user.setRole(UserRole.BARTENDER);
                            userService.updateUser(user);
                            logger.info("Admin '{}' promoted user '{}' to BARTENDER.", currentUser.getUsername(), user.getUsername());
                        }
                        break;
                    case DEMOTE_CASE:
                        if (user.getRole() == UserRole.BARTENDER) {
                            user.setRole(UserRole.CLIENT);
                            userService.updateUser(user);
                            logger.info("Admin '{}' demoted user '{}' to CLIENT.", currentUser.getUsername(), user.getUsername());
                        }
                        break;
                    default:
                        logger.warn("Unknown action '{}' attempted by admin '{}'", action, currentUser.getUsername());
                }
            }
        } catch (ServiceException e) {
            logger.error("Error updating user '{}' by admin '{}'", userId, currentUser.getUsername(), e);
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + PATH);
    }
}
