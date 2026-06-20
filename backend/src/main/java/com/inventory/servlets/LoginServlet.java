package com.inventory.servlets;

import com.inventory.dao.UserDAO;
import com.inventory.entities.User;
import com.inventory.entities.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("role", user.getRole().name());
            userData.put("fullName", user.getFullName());
            sendJson(resp, userData, "Session active");
        } else {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "No active session");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            LoginRequest credentials = parseJson(req, LoginRequest.class);
            if (credentials == null || credentials.username == null || credentials.password == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Username and password are required");
                return;
            }

            User user = userDAO.findByUsername(credentials.username);
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                return;
            }

            // Verify password using BCrypt
            if (!BCrypt.checkpw(credentials.password, user.getPassword())) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                return;
            }

            // Create HttpSession
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("role", user.getRole().name());
            userData.put("fullName", user.getFullName());

            sendJson(resp, userData, "Login successful");
        } catch (Exception e) {
            System.err.println("Error in LoginServlet.doPost: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred on the server");
        }
    }

    private static class LoginRequest {
        String username;
        String password;
    }
}
