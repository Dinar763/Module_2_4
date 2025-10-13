package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.File;
import exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import service.FileService;
import service.impl.FileServiceImpl;
import utill.HibernateUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

@MultipartConfig(
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
@WebServlet("/api/v1/files")
public class FileServlet extends HttpServlet {

    private final FileService fileService = FileServiceImpl.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                List<File> files = fileService.findAll();
                mapper.writeValue(resp.getWriter(), files);
            } else {
                Long id = Long.parseLong(idParam);
                File file = fileService.findById(id);
                mapper.writeValue(resp.getWriter(), file);
            }
        } catch (FileNotFoundException e) {
            resp.setStatus(404);
            resp.getWriter().print("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().print("{\"error\": \"Invalid ID format\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        } finally {
            HibernateUtil.closeCurrentSession();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            var userIdParam = req.getParameter("user_id");
            var filePart = req.getPart("file");

            if (userIdParam == null || filePart == null) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\": \"user_id and file are required\"}");
                return;
            }

            var userId = Long.valueOf(userIdParam);
            String fileName = getFileName(filePart);

            try (InputStream fileContent = filePart.getInputStream()) {
                File savedFile = fileService.uploadFile(fileName, fileContent, userId);

                resp.setStatus(201);
                resp.getWriter().write(
                        "{\"id\": " + savedFile.getId() +
                                ", \"name\": \"" + savedFile.getName() +
                                "\", \"user_id\": " + userId + "}"
                );
            }
        } catch (ServiceException  | FileAlreadyExistsException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
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
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");
            String nameParam = req.getParameter("name");

            String message;
            if (idParam != null) {
                Long id = Long.parseLong(idParam);
                fileService.deleteById(id);
                message = "File deleted successfully by ID";
            } else {
                fileService.deleteByName(nameParam);
                message = "File deleted successfully by name";
            }

            resp.getWriter().write("{\"message\": \"" + message + "\"}");
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
