package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import service.impl.UserServiceImpl;
import utill.HibernateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private final UserService userService = UserServiceImpl.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Operation(
            summary = "Get users",
            description = "Get all users or specific user by ID"
    )
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                List<User> users = userService.findAll();
                mapper.writeValue(resp.getWriter(), users);
            } else {
                Long id = Long.parseLong(idParam);
                User user = userService.findById(id);
                if (user != null) {
                    mapper.writeValue(resp.getWriter(), user);
                } else {
                    resp.setStatus(404);
                    resp.getWriter().print("{\"error\": \"User not found\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        } finally {
            HibernateUtil.closeCurrentSession();
        }
    }

    @Operation(summary = "Create user")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            JsonNode jsonNode = mapper.readTree(req.getReader());
            String userName = jsonNode.get("name").asText();

            User user = User.builder()
                            .name(userName)
                            .events(new ArrayList<>())
                            .build();

            User savedUser = userService.save(user);

            mapper.writeValue(resp.getWriter(), savedUser);

        } catch (JsonProcessingException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"Invalid JSON format\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            HibernateUtil.closeCurrentSession();
        }
    }

    @Operation(summary = "Update user")
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null || idParam.isBlank()) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\": \"ID parameter is required\"}");
                return;
            }

            Long id = Long.parseLong(idParam);
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                resp.setStatus(404);
                resp.getWriter().write("{\"error\": \"User not found\"}");
                return;
            }

            JsonNode jsonNode = mapper.readTree(req.getReader());
            String newName = jsonNode.get("name").asText();

            existingUser.setName(newName);
            User updatedUser = userService.update(existingUser);

            mapper.writeValue(resp.getWriter(), updatedUser);
        } catch (JsonProcessingException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"Invalid JSON format\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        } finally {
            HibernateUtil.closeCurrentSession();
        }

    }

    @Operation(summary = "Delete user")
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null || idParam.isBlank()) {
                resp.setStatus(400);
                resp.getWriter()
                    .write("{\"error\": \"ID parameter is required\"}");
                return;
            }

            Long id = Long.parseLong(idParam);
            userService.deleteById(id);

            resp.getWriter()
                .write("{\"message\": \"User deleted successfully\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        } finally {
            HibernateUtil.closeCurrentSession();
        }
    }
}
