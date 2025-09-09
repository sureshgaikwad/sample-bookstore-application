package com.bookstore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Root Controller - Handles root path requests
 * 
 * @author Suresh Gaikwad
 * @version 1.0.0
 */
@RestController
public class RootController {

    @Value("${info.app.name:Bookstore Application}")
    private String appName;

    @Value("${info.app.version:1.0.0}")
    private String appVersion;

    @Value("${info.app.author:Suresh Gaikwad}")
    private String appAuthor;

    /**
     * Root endpoint - redirects to web UI
     */
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/web/";
    }

    /**
     * API info endpoint - provides application information and available endpoints
     */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", appName);
        response.put("version", appVersion);
        response.put("author", appAuthor);
        response.put("status", "UP");
        response.put("message", "Welcome to the Bookstore API!");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("books", "/api/books");
        endpoints.put("health", "/actuator/health");
        endpoints.put("info", "/actuator/info");
        endpoints.put("search", "/api/books/search?q={searchTerm}");
        endpoints.put("by-author", "/api/books/author/{author}");
        endpoints.put("by-title", "/api/books/title/{title}");
        endpoints.put("in-stock", "/api/books/in-stock");
        
        response.put("endpoints", endpoints);
        
        Map<String, String> examples = new HashMap<>();
        examples.put("Get all books", "GET /api/books");
        examples.put("Get book by ID", "GET /api/books/1");
        examples.put("Search books", "GET /api/books/search?q=gatsby");
        examples.put("Create book", "POST /api/books (with JSON body)");
        
        response.put("examples", examples);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint at root level
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", appName);
        response.put("version", appVersion);
        return ResponseEntity.ok(response);
    }
}
