package com.inventory.servlets;

import com.inventory.dao.UserDAO;
import com.inventory.entities.User;
import com.inventory.entities.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.util.List;

public class UserServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<User> users = userDAO.findAll();
            // Clear passwords before returning to client for security
            if (users != null) {
                for (User u : users) {
                    u.setPassword(null);
                }
            }
            sendJson(resp, users, "Users list retrieved");
        } catch (Exception e) {
            System.err.println("Error in UserServlet.doGet: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserDTO dto = parseJson(req, UserDTO.class);
            if (dto == null || dto.username == null || dto.password == null || dto.fullName == null || dto.role == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields (username, password, fullName, role)");
                return;
            }

            // Check if username already exists
            if (userDAO.findByUsername(dto.username) != null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Username already exists");
                return;
            }

            // Hash password using BCrypt
            String hashedPassword = BCrypt.hashpw(dto.password, BCrypt.gensalt(10));

            User user = new User();
            user.setUsername(dto.username);
            user.setPassword(hashedPassword);
            user.setFullName(dto.fullName);
            user.setRole(Role.valueOf(dto.role.toUpperCase()));

            if (userDAO.save(user)) {
                user.setPassword(null); // Clear password hash before returning
                sendJson(resp, user, "User created successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to create user");
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid role name (must be ADMIN or STAFF)");
        } catch (Exception e) {
            System.err.println("Error in UserServlet.doPost: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    private static class UserDTO {
        String username;
        String password;
        String fullName;
        String role;
    }
}
