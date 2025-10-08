package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.Event;
import entity.File;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import service.EventService;
import service.FileService;
import service.UserService;
import service.impl.EventServiceImpl;
import service.impl.FileServiceImpl;
import service.impl.UserServiceImpl;
import utill.HibernateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@MultipartConfig(
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
@WebServlet("/files")
public class FileServlet extends HttpServlet {

    private final FileService fileService = FileServiceImpl.getInstance();
    private final UserService userService = UserServiceImpl.getInstance();
    private final EventService eventService = EventServiceImpl.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                List<File> files = fileService.findAll();
                mapper.writeValue(resp.getWriter(), files);
            } else {
                Long id = Long.parseLong(idParam);
                entity.File file = fileService.findById(id);
                if (file != null) {
                    mapper.writeValue(resp.getWriter(), file);
                } else {
                    resp.setStatus(404);
                    resp.getWriter().print("{\"error\": \"File not found\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        } finally {
            HibernateUtil.closeCurrentSession();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var userIdParam = req.getParameter("user_id");
            var filePart = req.getPart("file");

            if (userIdParam == null || filePart == null) {
                resp.setStatus(400);
                resp.getWriter()
                    .write("{\"error\": \"user_id and file are required\"}");
                return;
            }

            var userId = Long.valueOf(userIdParam);
            User user = userService.findById(userId);
            if (user == null) {
                resp.setStatus(404);
                resp.getWriter()
                    .write("{\"error\": \"User not found\"}");
                return;
            }

            String fileName = getFileName(filePart);

            try (InputStream fileContent = filePart.getInputStream()) {
                File savedFile = fileService.uploadFile(fileName, fileContent, userId);
                resp.sendRedirect("/files?success=true&id=" + savedFile.getId());

                resp.setContentType("application/json");
                resp.getWriter().write(
                        "{\"id\": " + savedFile.getId() +
                                ", \"name\": \"" + savedFile.getName() +
                                "\", \"user_id\": " + userId + "}"
                );
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            HibernateUtil.closeCurrentSession();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String idParam = req.getParameter("id");
            String nameParam = req.getParameter("name");

            if ((idParam == null || idParam.isBlank()) &&
                    (nameParam == null || nameParam.isBlank())) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\": \"ID or name parameter is required\"}");
                return;
            }

            if (idParam != null && !idParam.isBlank()) {
                Long id = Long.parseLong(idParam);
                fileService.deleteById(id);
                resp.getWriter().write("{\"message\": \"File deleted successfully by ID\"}");
            }

            else if (nameParam != null && !nameParam.isBlank()) {
                fileService.deleteByName(nameParam);
                resp.getWriter().write("{\"message\": \"File deleted successfully by name\"}");
            }
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

    private String getFileName(Part part) {
        String contentDispos = part.getHeader("content-disposition");
        for (String value : contentDispos.split(";")) {
            if (value.trim().startsWith("filename")) {
                return value.substring(value.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }
}
