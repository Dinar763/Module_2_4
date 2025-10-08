package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.Event;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.EventService;
import service.impl.EventServiceImpl;
import utill.HibernateUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/events")
public class EventServlet extends HttpServlet {

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
            String userIdParam = req.getParameter("id");

            if (userIdParam == null || userIdParam.isEmpty()) {
                List<Event> events = eventService.findAll();
                mapper.writeValue(resp.getWriter(), events);
            } else {
                Long userId = Long.parseLong(userIdParam);
                List<Event> events = eventService.findAllByUserId(userId);
                if (events != null) {
                    mapper.writeValue(resp.getWriter(), events);
                } else {
                    resp.setStatus(404);
                    resp.getWriter().print("{\"error\": \"Events by this user not found\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        } finally {
            HibernateUtil.closeCurrentSession();
        }
    }
}
