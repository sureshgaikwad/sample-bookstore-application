package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Simple Web Controller - Basic HTML without Thymeleaf for testing
 * 
 * @author Suresh Gaikwad
 * @version 1.0.0
 */
@Controller
public class SimpleWebController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "/simple", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String simpleBookList() {
        List<Book> books = bookService.getAllBooks();
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Bookstore - Simple View</title>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        html.append("</head>");
        html.append("<body>");
        
        // Navigation
        html.append("<nav class='navbar navbar-dark bg-dark'>");
        html.append("<div class='container'>");
        html.append("<a class='navbar-brand' href='/simple'>📚 Bookstore</a>");
        html.append("<div>");
        html.append("<a class='btn btn-outline-light me-2' href='/api/books'>API</a>");
        html.append("<a class='btn btn-outline-light' href='/actuator/health'>Health</a>");
        html.append("</div>");
        html.append("</div>");
        html.append("</nav>");
        
        // Main content
        html.append("<div class='container mt-4'>");
        html.append("<h1>Bookstore Application</h1>");
        html.append("<p class='text-muted'>Created by Suresh Gaikwad</p>");
        html.append("<hr>");
        
        if (books.isEmpty()) {
            html.append("<div class='alert alert-info'>");
            html.append("<h4>No books found</h4>");
            html.append("<p>The bookstore is empty. Add some books via the API:</p>");
            html.append("<pre>curl -X POST http://your-url/api/books -H 'Content-Type: application/json' -d '{...}'</pre>");
            html.append("</div>");
        } else {
            html.append("<h2>Available Books (" + books.size() + ")</h2>");
            html.append("<div class='row'>");
            
            for (Book book : books) {
                html.append("<div class='col-md-4 mb-3'>");
                html.append("<div class='card'>");
                html.append("<div class='card-body'>");
                html.append("<h5 class='card-title'>").append(escapeHtml(book.getTitle())).append("</h5>");
                html.append("<h6 class='card-subtitle mb-2 text-muted'>by ").append(escapeHtml(book.getAuthor())).append("</h6>");
                
                if (book.getDescription() != null && !book.getDescription().isEmpty()) {
                    String desc = book.getDescription();
                    if (desc.length() > 100) {
                        desc = desc.substring(0, 97) + "...";
                    }
                    html.append("<p class='card-text'>").append(escapeHtml(desc)).append("</p>");
                }
                
                html.append("<div class='d-flex justify-content-between align-items-center'>");
                html.append("<span class='badge bg-success'>$").append(book.getPrice()).append("</span>");
                
                if (book.getStockQuantity() != null && book.getStockQuantity() > 0) {
                    html.append("<span class='badge bg-primary'>").append(book.getStockQuantity()).append(" in stock</span>");
                } else {
                    html.append("<span class='badge bg-danger'>Out of stock</span>");
                }
                html.append("</div>");
                
                html.append("<div class='mt-2'>");
                html.append("<small class='text-muted'>ISBN: ").append(escapeHtml(book.getIsbn())).append("</small>");
                if (book.getPublicationYear() != null) {
                    html.append("<br><small class='text-muted'>Published: ").append(book.getPublicationYear()).append("</small>");
                }
                html.append("</div>");
                
                html.append("</div>");
                html.append("</div>");
                html.append("</div>");
            }
            html.append("</div>");
        }
        
        // API Links
        html.append("<hr>");
        html.append("<h3>API Endpoints</h3>");
        html.append("<div class='row'>");
        html.append("<div class='col-md-6'>");
        html.append("<h5>REST API</h5>");
        html.append("<ul class='list-unstyled'>");
        html.append("<li><a href='/api/books'>GET /api/books</a> - All books</li>");
        html.append("<li><a href='/api/books/search?q=gatsby'>GET /api/books/search?q=gatsby</a> - Search</li>");
        html.append("<li><a href='/api/books/in-stock'>GET /api/books/in-stock</a> - In stock</li>");
        html.append("</ul>");
        html.append("</div>");
        html.append("<div class='col-md-6'>");
        html.append("<h5>Management</h5>");
        html.append("<ul class='list-unstyled'>");
        html.append("<li><a href='/actuator/health'>GET /actuator/health</a> - Health check</li>");
        html.append("<li><a href='/actuator/info'>GET /actuator/info</a> - App info</li>");
        html.append("<li><a href='/'>GET /</a> - Root redirect</li>");
        html.append("</ul>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("</div>");
        
        // Footer
        html.append("<footer class='bg-dark text-light py-3 mt-5'>");
        html.append("<div class='container text-center'>");
        html.append("<p>&copy; 2024 Bookstore Application - Created by <strong>Suresh Gaikwad</strong></p>");
        html.append("</div>");
        html.append("</footer>");
        
        html.append("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
