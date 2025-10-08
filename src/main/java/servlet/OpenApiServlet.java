package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/openapi.json")
public class OpenApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String openApiSpec = """
    {
        "openapi": "3.0.0",
        "info": {
            "title": "File Manager API",
            "version": "1.0.0",
            "description": "API for managing users, files and events"
        },
        "paths": {
            "/users": {
                "get": {
                    "summary": "Get all users",
                    "description": "Returns list of all users or specific user by ID",
                    "parameters": [
                        {
                            "name": "id",
                            "in": "query",
                            "description": "User ID",
                            "required": false,
                            "schema": {
                                "type": "integer"
                            }
                        }
                    ],
                    "responses": {
                        "200": {
                            "description": "Successful operation",
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "oneOf": [
                                            {
                                                "type": "array",
                                                "items": {
                                                    "$ref": "#/components/schemas/User"
                                                }
                                            },
                                            {
                                                "$ref": "#/components/schemas/User"
                                            }
                                        ]
                                    }
                                }
                            }
                        }
                    }
                },
                "post": {
                    "summary": "Create user",
                    "description": "Create a new user",
                    "requestBody": {
                        "required": true,
                        "content": {
                            "application/json": {
                                "schema": {
                                    "type": "object",
                                    "properties": {
                                        "name": {
                                            "type": "string",
                                            "example": "username"
                                        }
                                    },
                                    "required": ["name"]
                                }
                            }
                        }
                    },
                    "responses": {
                        "200": {
                            "description": "User created successfully",
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "$ref": "#/components/schemas/User"
                                    }
                                }
                            }
                        }
                    }
                },
                "delete": {
                    "summary": "Delete user",
                    "description": "Delete user by ID",
                    "parameters": [
                        {
                            "name": "id",
                            "in": "query",
                            "description": "User ID",
                            "required": true,
                            "schema": {
                                "type": "integer",
                                "example": 1
                            }
                        }
                    ],
                    "responses": {
                        "200": {
                            "description": "User deleted successfully"
                        },
                        "404": {
                            "description": "User not found"
                        }
                    }
                }
            },
            "/files": {
                "get": {
                    "summary": "Get files",
                    "description": "Get all files or specific file by ID",
                    "parameters": [
                        {
                            "name": "id",
                            "in": "query",
                            "description": "File ID",
                            "required": false,
                            "schema": {
                                "type": "integer"
                            }
                        }
                    ],
                    "responses": {
                        "200": {
                            "description": "Successful operation",
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "oneOf": [
                                            {
                                                "type": "array",
                                                "items": {
                                                    "$ref": "#/components/schemas/File"
                                                }
                                            },
                                            {
                                                "$ref": "#/components/schemas/File"
                                            }
                                        ]
                                    }
                                }
                            }
                        }
                    }
                },
                "post": {
                    "summary": "Upload file",
                    "description": "Upload a file",
                    "requestBody": {
                        "required": true,
                        "content": {
                            "multipart/form-data": {
                                "schema": {
                                    "type": "object",
                                    "properties": {
                                        "file": {
                                            "type": "string",
                                            "format": "binary",
                                            "description": "File to upload"
                                        },
                                        "user_id": {
                                            "type": "integer",
                                            "example": 1
                                        }
                                    },
                                    "required": ["file", "user_id"]
                                }
                            }
                        }
                    },
                    "responses": {
                        "200": {
                            "description": "File uploaded successfully",
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "$ref": "#/components/schemas/File"
                                    }
                                }
                            }
                        }
                    }
                },
                "delete": {
                    "summary": "Delete file",
                    "description": "Delete file by ID or by name",
                    "parameters": [
                        {
                            "name": "id",
                            "in": "query",
                            "description": "File ID",
                            "required": false,
                            "schema": {
                                "type": "integer"
                            }
                        },
                        {
                            "name": "name",
                            "in": "query",
                            "description": "File name",
                            "required": false,
                            "schema": {
                                "type": "string",
                                "example": "123.txt"
                            }
                        }
                    ],
                    "responses": {
                        "200": {
                            "description": "File deleted successfully"
                        },
                        "400": {
                            "description": "Either id or name parameter must be provided"
                        }
                    }
                }
            },
            "/events": {
                "get": {
                    "summary": "Get events",
                    "description": "Get all events or specific event by ID",
                    "parameters": [
                        {
                            "name": "id",
                            "in": "query",
                            "description": "Event ID",
                            "required": false,
                            "schema": {
                                "type": "integer"
                            }
                        }
                    ],
                    "responses": {
                        "200": {
                            "description": "Successful operation",
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "oneOf": [
                                            {
                                                "type": "array",
                                                "items": {
                                                    "$ref": "#/components/schemas/Event"
                                                }
                                            },
                                            {
                                                "$ref": "#/components/schemas/Event"
                                            }
                                        ]
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        "components": {
            "schemas": {
                "User": {
                    "type": "object",
                    "properties": {
                        "id": {
                            "type": "integer",
                            "example": 1
                        },
                        "name": {
                            "type": "string",
                            "example": "username"
                        }
                    }
                },
                "File": {
                    "type": "object",
                    "properties": {
                        "id": {
                            "type": "integer",
                            "example": 1
                        },
                        "name": {
                            "type": "string",
                            "example": "document.pdf"
                        },
                        "filePath": {
                            "type": "string",
                            "example": "/uploads/document.pdf"
                        },
                        "userId": {
                            "type": "integer",
                            "example": 1
                        }
                    }
                },
                "Event": {
                    "type": "object",
                    "properties": {
                        "id": {
                            "type": "integer",
                            "example": 1
                        },
                        "userId": {
                            "type": "integer",
                            "example": 1
                        },
                        "fileId": {
                            "type": "integer",
                            "example": 1
                        },
                        "type": {
                            "type": "string",
                            "example": "UPLOAD"
                        },
                        "timestamp": {
                            "type": "string",
                            "format": "date-time"
                        }
                    }
                }
            }
        }
    }
    """;

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(openApiSpec);
        out.flush();
    }
}
