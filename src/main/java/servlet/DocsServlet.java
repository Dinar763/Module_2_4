package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/docs")
public class DocsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>API Documentation</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .endpoint { background: #f5f5f5; padding: 15px; margin: 10px 0; border-left: 4px solid #007cba; }
                    .method { font-weight: bold; color: #007cba; }
                    .url { font-family: monospace; }
                </style>
            </head>
            <body>
                <h1>File Manager API Documentation</h1>
                
                <h2>Users Endpoints</h2>
                <div class="endpoint">
                    <span class="method">GET</span> <span class="url">/users</span><br>
                    Get all users
                </div>
                <div class="endpoint">
                    <span class="method">GET</span> <span class="url">/users?id=1</span><br>
                    Get user by ID
                </div>
                <div class="endpoint">
                    <span class="method">POST</span> <span class="url">/users</span><br>
                    Create user<br>
                    <strong>Body:</strong> {"name": "username"}
                </div>
                </div>
                <div class="endpoint">
                    <span class="method">DELETE</span> <span class="url">/users?id=1</span><br>
                    Delete user by ID<br>
                </div>
                
                <h2>Files Endpoints</h2>
                <div class="endpoint">
                    <span class="method">POST</span> <span class="url">/files</span><br>
                    Upload file<br>
                    <strong>Form data:</strong> file=[file], user_id=1
                </div>
                <div class="endpoint">
                    <span class="method">GET</span> <span class="url">/files</span><br>
                    Get all files<br>
                </div>
                <div class="endpoint">
                    <span class="method">GET</span> <span class="url">/files?id=1</span><br>
                    Get file by ID<br>
                </div>
                <div class="endpoint">
                    <span class="method">DELETE</span> <span class="url">/files?id=1</span><br>
                    Delete file by ID<br>
                </div>
                <div class="endpoint">
                    <span class="method">DELETE</span> <span class="url">/files?name=123.txt</span><br>
                    Delete file by name<br>
                </div>
                
                <h2>Events Endpoints</h2>
                <div class="endpoint">
                    <span class="method">GET</span> <span class="url">/events</span><br>
                    Get all events
                </div>
                <div class="endpoint">
                    <span class="method">GET</span> <span class="url">/events?id=1</span><br>
                    Get event by ID
                </div>
               
            </body>
            </html>
            """;

        resp.setContentType("text/html");
        resp.getWriter().write(html);
    }
}
