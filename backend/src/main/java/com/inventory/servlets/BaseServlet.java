package com.inventory.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected static final Gson gson = new Gson();

    protected void sendJson(HttpServletResponse resp, Object data, String message) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        JsonObject responseJson = new JsonObject();
        if (message != null) {
            responseJson.addProperty("message", message);
        }
        if (data != null) {
            responseJson.add("data", gson.toJsonTree(data));
        }
        resp.getWriter().write(responseJson.toString());
    }

    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(status);
        
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("error", message);
        responseJson.addProperty("code", status);
        resp.getWriter().write(responseJson.toString());
    }

    protected <T> T parseJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        return gson.fromJson(req.getReader(), clazz);
    }
}
