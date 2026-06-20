package com.inventory.filters;

import com.inventory.entities.Role;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // 1. Bypass login endpoint
        if (path.startsWith("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Intercept all other /api/* requests
        if (path.startsWith("/api/")) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                sendErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Please log in");
                return;
            }

            Role role = (Role) session.getAttribute("role");
            String method = req.getMethod();

            // 3. Role enforcement logic
            boolean isStaff = (role == Role.STAFF);

            if (isStaff) {
                // STAFF cannot write to products or categories
                if (path.startsWith("/api/products") && !method.equalsIgnoreCase("GET")) {
                    sendErrorResponse(res, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Admin role required for write operations");
                    return;
                }
                if (path.startsWith("/api/categories") && !method.equalsIgnoreCase("GET")) {
                    sendErrorResponse(res, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Admin role required for write operations");
                    return;
                }
                // STAFF cannot access suppliers at all
                if (path.startsWith("/api/suppliers")) {
                    sendErrorResponse(res, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Admin role required");
                    return;
                }
                // STAFF cannot access user management at all
                if (path.startsWith("/api/users")) {
                    sendErrorResponse(res, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Admin role required");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\", \"code\": %d}", message, status));
    }

    @Override
    public void destroy() {}
}
