package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/swagger")
public class SwaggerUIServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>File Manager API - Swagger UI</title>
            <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/swagger-ui-dist@3.52.5/swagger-ui.css">
            <style>
                .swagger-ui .topbar { display: none; }
                .information-container { margin: 20px 0; }
                .download-url-wrapper { display: none; }
            </style>
        </head>
        <body>
            <div id="swagger-ui"></div>
            <script src="https://cdn.jsdelivr.net/npm/swagger-ui-dist@3.52.5/swagger-ui-bundle.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/swagger-ui-dist@3.52.5/swagger-ui-standalone-preset.js"></script>
            <script>
            window.onload = function() {
                const ui = SwaggerUIBundle({
                    url: "/openapi.json",
                    dom_id: '#swagger-ui',
                    deepLinking: true,
                    presets: [
                        SwaggerUIBundle.presets.apis,
                        SwaggerUIStandalonePreset
                    ],
                    plugins: [
                        SwaggerUIBundle.plugins.DownloadUrl
                    ],
                    layout: "StandaloneLayout",
                    defaultModelsExpandDepth: 1,
                    defaultModelExpandDepth: 1,
                    docExpansion: "list",
                    filter: true,
                    showExtensions: true,
                    showCommonExtensions: true,
                    supportedSubmitMethods: ["get", "post", "put", "delete", "patch"],
                    validatorUrl: null
                });
                
                window.ui = ui;
            };
            </script>
        </body>
        </html>
        """;

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(html);
        out.flush();
    }
}
