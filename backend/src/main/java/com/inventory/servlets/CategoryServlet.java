package com.inventory.servlets;

import com.inventory.dao.CategoryDAO;
import com.inventory.entities.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryServlet extends BaseServlet {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            // GET /api/categories/:id
            if (pathInfo != null && pathInfo.length() > 1) {
                Integer id = Integer.parseInt(pathInfo.substring(1));
                Category category = categoryDAO.findById(id);
                if (category != null) {
                    sendJson(resp, category, "Category details retrieved");
                } else {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found");
                }
                return;
            }

            // GET /api/categories
            List<Category> categories = categoryDAO.findAll();
            sendJson(resp, categories, "All categories retrieved");
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID format");
        } catch (Exception e) {
            System.err.println("Error in CategoryServlet.doGet: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            CategoryDTO dto = parseJson(req, CategoryDTO.class);
            if (dto == null || dto.name == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category name is required");
                return;
            }

            Category category = new Category();
            category.setName(dto.name);
            category.setDescription(dto.description);

            if (categoryDAO.save(category)) {
                sendJson(resp, category, "Category created successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to create category. Name must be unique.");
            }
        } catch (Exception e) {
            System.err.println("Error in CategoryServlet.doPost: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category ID required for update");
            return;
        }

        try {
            Integer id = Integer.parseInt(pathInfo.substring(1));
            Category existingCategory = categoryDAO.findById(id);
            if (existingCategory == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found");
                return;
            }

            CategoryDTO dto = parseJson(req, CategoryDTO.class);
            if (dto == null || dto.name == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category name is required");
                return;
            }

            existingCategory.setName(dto.name);
            existingCategory.setDescription(dto.description);

            if (categoryDAO.update(existingCategory)) {
                sendJson(resp, existingCategory, "Category updated successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to update category. Name must be unique.");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID format");
        } catch (Exception e) {
            System.err.println("Error in CategoryServlet.doPut: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category ID required for delete");
            return;
        }

        try {
            Integer id = Integer.parseInt(pathInfo.substring(1));
            if (categoryDAO.delete(id)) {
                sendJson(resp, null, "Category deleted successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID format");
        } catch (Exception e) {
            System.err.println("Error in CategoryServlet.doDelete: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    private static class CategoryDTO {
        String name;
        String description;
    }
}
