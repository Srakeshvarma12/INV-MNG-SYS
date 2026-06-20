package com.inventory.servlets;

import com.inventory.dao.StockUpdateDAO;
import com.inventory.entities.StockUpdate;
import com.inventory.entities.Product;
import com.inventory.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class StockServlet extends BaseServlet {
    private final StockUpdateDAO stockUpdateDAO = new StockUpdateDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<StockUpdate> stockHistory = stockUpdateDAO.findAllWithDetails();
            sendJson(resp, stockHistory, "Stock update history retrieved");
        } catch (Exception e) {
            System.err.println("Error in StockServlet.doGet: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized session");
                return;
            }

            User currentUser = (User) session.getAttribute("user");

            StockDTO dto = parseJson(req, StockDTO.class);
            if (dto == null || dto.productId == null || dto.changeQty == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields (productId, changeQty)");
                return;
            }

            Product product = new Product();
            product.setId(dto.productId);

            StockUpdate update = new StockUpdate();
            update.setProduct(product);
            update.setUpdatedBy(currentUser);
            update.setChangeQty(dto.changeQty);
            update.setNote(dto.note);

            if (stockUpdateDAO.save(update)) {
                sendJson(resp, update, "Stock updated successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to update stock. Product might not exist.");
            }
        } catch (Exception e) {
            System.err.println("Error in StockServlet.doPost: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    private static class StockDTO {
        Integer productId;
        Integer changeQty;
        String note;
    }
}
