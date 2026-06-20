package com.inventory.servlets;

import com.inventory.dao.ProductDAO;
import com.inventory.entities.Product;
import com.inventory.entities.Category;
import com.inventory.entities.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ProductServlet extends BaseServlet {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            // Case 1: GET /api/products/:id
            if (pathInfo != null && pathInfo.length() > 1) {
                Integer id = Integer.parseInt(pathInfo.substring(1));
                Product product = productDAO.findById(id);
                if (product != null) {
                    sendJson(resp, product, "Product details retrieved");
                } else {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
                }
                return;
            }

            // Case 2: GET /api/products?lowStock=true
            String lowStockParam = req.getParameter("lowStock");
            if ("true".equalsIgnoreCase(lowStockParam)) {
                List<Product> lowStockProducts = productDAO.findLowStock();
                sendJson(resp, lowStockProducts, "Low-stock products list retrieved");
                return;
            }

            // Case 3: GET /api/products
            List<Product> products = productDAO.findAllWithCategory();
            sendJson(resp, products, "All products retrieved");
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
        } catch (Exception e) {
            System.err.println("Error in ProductServlet.doGet: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ProductDTO dto = parseJson(req, ProductDTO.class);
            if (dto == null || dto.name == null || dto.sku == null || dto.categoryId == null || dto.price == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields (name, sku, categoryId, price)");
                return;
            }

            Product product = new Product();
            product.setName(dto.name);
            product.setSku(dto.sku);
            product.setPrice(dto.price);
            product.setQuantity(dto.quantity != null ? dto.quantity : 0);
            product.setMinQuantity(dto.minQuantity != null ? dto.minQuantity : 5);

            Category category = new Category();
            category.setId(dto.categoryId);
            product.setCategory(category);

            if (dto.supplierId != null) {
                Supplier supplier = new Supplier();
                supplier.setId(dto.supplierId);
                product.setSupplier(supplier);
            }

            if (productDAO.save(product)) {
                sendJson(resp, product, "Product created successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to create product. Ensure SKU is unique.");
            }
        } catch (Exception e) {
            System.err.println("Error in ProductServlet.doPost: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product ID required for update");
            return;
        }

        try {
            Integer id = Integer.parseInt(pathInfo.substring(1));
            Product existingProduct = productDAO.findById(id);
            if (existingProduct == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
                return;
            }

            ProductDTO dto = parseJson(req, ProductDTO.class);
            if (dto == null || dto.name == null || dto.sku == null || dto.categoryId == null || dto.price == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
                return;
            }

            existingProduct.setName(dto.name);
            existingProduct.setSku(dto.sku);
            existingProduct.setPrice(dto.price);
            existingProduct.setQuantity(dto.quantity != null ? dto.quantity : existingProduct.getQuantity());
            existingProduct.setMinQuantity(dto.minQuantity != null ? dto.minQuantity : existingProduct.getMinQuantity());

            Category category = new Category();
            category.setId(dto.categoryId);
            existingProduct.setCategory(category);

            if (dto.supplierId != null) {
                Supplier supplier = new Supplier();
                supplier.setId(dto.supplierId);
                existingProduct.setSupplier(supplier);
            } else {
                existingProduct.setSupplier(null);
            }

            if (productDAO.update(existingProduct)) {
                sendJson(resp, existingProduct, "Product updated successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to update product. SKU may already be in use.");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
        } catch (Exception e) {
            System.err.println("Error in ProductServlet.doPut: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product ID required for delete");
            return;
        }

        try {
            Integer id = Integer.parseInt(pathInfo.substring(1));
            if (productDAO.delete(id)) {
                sendJson(resp, null, "Product deleted successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
        } catch (Exception e) {
            System.err.println("Error in ProductServlet.doDelete: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    private static class ProductDTO {
        String name;
        String sku;
        Integer categoryId;
        Integer supplierId;
        BigDecimal price;
        Integer quantity;
        Integer minQuantity;
    }
}
