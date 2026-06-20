package com.inventory.servlets;

import com.inventory.dao.SupplierDAO;
import com.inventory.entities.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SupplierServlet extends BaseServlet {
    private final SupplierDAO supplierDAO = new SupplierDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        try {
            // GET /api/suppliers/:id
            if (pathInfo != null && pathInfo.length() > 1) {
                Integer id = Integer.parseInt(pathInfo.substring(1));
                Supplier supplier = supplierDAO.findById(id);
                if (supplier != null) {
                    sendJson(resp, supplier, "Supplier details retrieved");
                } else {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Supplier not found");
                }
                return;
            }

            // GET /api/suppliers
            List<Supplier> suppliers = supplierDAO.findAll();
            sendJson(resp, suppliers, "All suppliers retrieved");
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid supplier ID format");
        } catch (Exception e) {
            System.err.println("Error in SupplierServlet.doGet: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            SupplierDTO dto = parseJson(req, SupplierDTO.class);
            if (dto == null || dto.name == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Supplier name is required");
                return;
            }

            Supplier supplier = new Supplier();
            supplier.setName(dto.name);
            supplier.setContact(dto.contact);
            supplier.setEmail(dto.email);
            supplier.setPhone(dto.phone);

            if (supplierDAO.save(supplier)) {
                sendJson(resp, supplier, "Supplier created successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to create supplier");
            }
        } catch (Exception e) {
            System.err.println("Error in SupplierServlet.doPost: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Supplier ID required for update");
            return;
        }

        try {
            Integer id = Integer.parseInt(pathInfo.substring(1));
            Supplier existingSupplier = supplierDAO.findById(id);
            if (existingSupplier == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Supplier not found");
                return;
            }

            SupplierDTO dto = parseJson(req, SupplierDTO.class);
            if (dto == null || dto.name == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Supplier name is required");
                return;
            }

            existingSupplier.setName(dto.name);
            existingSupplier.setContact(dto.contact);
            existingSupplier.setEmail(dto.email);
            existingSupplier.setPhone(dto.phone);

            if (supplierDAO.update(existingSupplier)) {
                sendJson(resp, existingSupplier, "Supplier updated successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to update supplier");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid supplier ID format");
        } catch (Exception e) {
            System.err.println("Error in SupplierServlet.doPut: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Supplier ID required for delete");
            return;
        }

        try {
            Integer id = Integer.parseInt(pathInfo.substring(1));
            if (supplierDAO.delete(id)) {
                sendJson(resp, null, "Supplier deleted successfully");
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Supplier not found");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid supplier ID format");
        } catch (Exception e) {
            System.err.println("Error in SupplierServlet.doDelete: " + e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred");
        }
    }

    private static class SupplierDTO {
        String name;
        String contact;
        String email;
        String phone;
    }
}
