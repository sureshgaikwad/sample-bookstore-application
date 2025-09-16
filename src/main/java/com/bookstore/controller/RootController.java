package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Root Controller - Handles root path requests
 * 
 * @author Suresh Gaikwad
 * @version 1.0.0
 */
@RestController
public class RootController {

    @Autowired
    private BookService bookService;

    @Value("${info.app.name:Bookstore Application}")
    private String appName;

    @Value("${info.app.version:1.0.0}")
    private String appVersion;

    @Value("${info.app.author:Suresh Gaikwad}")
    private String appAuthor;

    /**
     * Root endpoint - serves beautiful bookstore web UI with book listings
     */
    @GetMapping(value = "/", produces = "text/html")
    @ResponseBody
    public String rootPage() {
        List<Book> books = bookService.getAllBooks();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>").append(escapeHtml(appName)).append("</title>");
        html.append("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        html.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css' rel='stylesheet'>");
        html.append("<style>");
        html.append(".book-card { transition: transform 0.2s, box-shadow 0.2s; }");
        html.append(".book-card:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(0,0,0,0.15); }");
        html.append(".book-price { font-size: 1.25rem; font-weight: bold; color: #28a745; }");
        html.append(".book-stock { font-size: 0.9rem; }");
        html.append(".hero-section { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 60px 0; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        
        // Navigation
        html.append("<nav class='navbar navbar-expand-lg navbar-dark bg-dark sticky-top'>");
        html.append("<div class='container'>");
        html.append("<a class='navbar-brand' href='/'><i class='fas fa-book'></i> ").append(escapeHtml(appName)).append("</a>");
        html.append("<button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav'>");
        html.append("<span class='navbar-toggler-icon'></span>");
        html.append("</button>");
        html.append("<div class='collapse navbar-collapse' id='navbarNav'>");
        html.append("<ul class='navbar-nav ms-auto'>");
        html.append("<li class='nav-item'><a class='nav-link' href='/api/books'><i class='fas fa-code'></i> API</a></li>");
        html.append("<li class='nav-item'><a class='nav-link' href='/actuator/health'><i class='fas fa-heartbeat'></i> Health</a></li>");
        html.append("</ul>");
        html.append("</div>");
        html.append("</div>");
        html.append("</nav>");
        
        // Hero Section
        html.append("<div class='hero-section text-center'>");
        html.append("<div class='container'>");
        html.append("<h1 class='display-4 mb-3'><i class='fas fa-book-open'></i> Welcome to Our Bookstore</h1>");
        html.append("<p class='lead'>Discover amazing books from our curated collection</p>");
        html.append("<p class='mb-0'>Created for Java event - Pipeline Working!</p>");
        html.append("</div>");
        html.append("</div>");
        
        // Main Content
        html.append("<div class='container my-5'>");
        
        if (books.isEmpty()) {
            html.append("<div class='text-center py-5'>");
            html.append("<i class='fas fa-book fa-5x text-muted mb-4'></i>");
            html.append("<h3>No Books Available</h3>");
            html.append("<p class='text-muted'>Our bookstore is currently empty. Please check back later!</p>");
            html.append("</div>");
        } else {
            html.append("<div class='d-flex justify-content-between align-items-center mb-4'>");
            html.append("<h2><i class='fas fa-books'></i> Our Book Collection</h2>");
            html.append("<span class='badge bg-primary fs-6'>").append(books.size()).append(" books available</span>");
            html.append("</div>");
            
            html.append("<div class='row g-4'>");
            
            for (Book book : books) {
                html.append("<div class='col-lg-4 col-md-6'>");
                html.append("<div class='card h-100 book-card border-0 shadow-sm'>");
                html.append("<div class='card-body d-flex flex-column'>");
                
                // Book Title
                html.append("<h5 class='card-title text-primary mb-2'>");
                html.append("<i class='fas fa-book'></i> ").append(escapeHtml(book.getTitle()));
                html.append("</h5>");
                
                // Author
                html.append("<h6 class='card-subtitle mb-3 text-muted'>");
                html.append("<i class='fas fa-user'></i> by ").append(escapeHtml(book.getAuthor()));
                html.append("</h6>");
                
                // Description
                if (book.getDescription() != null && !book.getDescription().isEmpty()) {
                    String description = book.getDescription();
                    if (description.length() > 120) {
                        description = description.substring(0, 117) + "...";
                    }
                    html.append("<p class='card-text flex-grow-1'>").append(escapeHtml(description)).append("</p>");
                }
                
                // Book Details
                html.append("<div class='mt-auto'>");
                html.append("<div class='row g-2 mb-3'>");
                
                // Price
                html.append("<div class='col-6'>");
                html.append("<div class='book-price text-success'>");
                html.append("<i class='fas fa-dollar-sign'></i> ").append(currencyFormat.format(book.getPrice()));
                html.append("</div>");
                html.append("</div>");
                
                // Stock
                html.append("<div class='col-6 text-end'>");
                if (book.getStockQuantity() != null && book.getStockQuantity() > 0) {
                    String stockClass = book.getStockQuantity() > 10 ? "text-success" : 
                                       book.getStockQuantity() > 5 ? "text-warning" : "text-danger";
                    html.append("<span class='book-stock ").append(stockClass).append("'>");
                    html.append("<i class='fas fa-boxes'></i> ").append(book.getStockQuantity()).append(" in stock");
                    html.append("</span>");
                } else {
                    html.append("<span class='book-stock text-danger'>");
                    html.append("<i class='fas fa-times-circle'></i> Out of stock");
                    html.append("</span>");
                }
                html.append("</div>");
                html.append("</div>");
                
                // Additional Info
                html.append("<div class='d-flex justify-content-between align-items-center'>");
                html.append("<small class='text-muted'>");
                if (book.getPublicationYear() != null) {
                    html.append("<i class='fas fa-calendar'></i> ").append(book.getPublicationYear());
                }
                html.append("</small>");
                html.append("<small class='text-muted'>");
                html.append("<i class='fas fa-barcode'></i> ").append(escapeHtml(book.getIsbn()));
                html.append("</small>");
                html.append("</div>");
                
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
            }
            html.append("</div>");
        }
        
        // API Section
        html.append("<hr class='my-5'>");
        html.append("<div class='row'>");
        html.append("<div class='col-lg-8 mx-auto'>");
        html.append("<h3 class='text-center mb-4'><i class='fas fa-code'></i> Developer API</h3>");
        html.append("<div class='row g-3'>");
        
        String[][] endpoints = {
            {"GET /api/books", "Get all books", "fas fa-list", "primary", "/api/books"},
            {"GET /api/books/search", "Search books", "fas fa-search", "info", "/api/books/search?q=gatsby"},
            {"GET /api/books/in-stock", "Books in stock", "fas fa-check-circle", "success", "/api/books/in-stock"},
            {"GET /actuator/health", "Health check", "fas fa-heartbeat", "danger", "/actuator/health"}
        };
        
        for (String[] endpoint : endpoints) {
            html.append("<div class='col-md-6'>");
            html.append("<a href='").append(endpoint[4]).append("' class='text-decoration-none'>");
            html.append("<div class='card border-").append(endpoint[3]).append(" h-100'>");
            html.append("<div class='card-body text-center'>");
            html.append("<i class='").append(endpoint[2]).append(" fa-2x text-").append(endpoint[3]).append(" mb-2'></i>");
            html.append("<h6 class='card-title'>").append(endpoint[0]).append("</h6>");
            html.append("<p class='card-text small text-muted'>").append(endpoint[1]).append("</p>");
            html.append("</div>");
            html.append("</div>");
            html.append("</a>");
            html.append("</div>");
        }
        
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("</div>");
        
        // Footer
        html.append("<footer class='bg-dark text-light py-4 mt-5'>");
        html.append("<div class='container text-center'>");
        html.append("<p class='mb-2'>&copy; 2024 ").append(escapeHtml(appName)).append(" - Version ").append(escapeHtml(appVersion)).append("</p>");
        html.append("<p class='mb-0'>Built with <i class='fas fa-heart text-danger'></i> by <strong>").append(escapeHtml(appAuthor)).append("</strong></p>");
        html.append("</div>");
        html.append("</footer>");
        
        html.append("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * Escapes HTML special characters to prevent XSS
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
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
