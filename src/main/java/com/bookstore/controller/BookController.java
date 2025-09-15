package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Rating;
import com.bookstore.service.BookService;
import com.bookstore.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private RatingService ratingService;
    
    /**
     * Get all books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get book by ISBN
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new book
     */
    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Update an existing book
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Delete a book
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Search books by author or title
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String q) {
        List<Book> books = bookService.searchBooks(q);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get books by author
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        List<Book> books = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get books by title
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        List<Book> books = bookService.getBooksByTitle(title);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get books in stock
     */
    @GetMapping("/in-stock")
    public ResponseEntity<List<Book>> getBooksInStock() {
        List<Book> books = bookService.getBooksInStock();
        return ResponseEntity.ok(books);
    }
    
    /**
     * Update stock quantity
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            Book updatedBook = bookService.updateStock(id, quantity);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Rating endpoints
    
    /**
     * Add a rating for a book
     */
    @PostMapping("/{id}/ratings")
    public ResponseEntity<?> addRating(@PathVariable Long id, @RequestBody Map<String, Object> ratingData) {
        try {
            Integer rating = (Integer) ratingData.get("rating");
            String reviewerName = (String) ratingData.get("reviewerName");
            String comment = (String) ratingData.get("comment");
            
            if (rating == null || rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
            }
            
            if (reviewerName == null || reviewerName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reviewer name is required");
            }
            
            Rating newRating = ratingService.addRating(id, rating, reviewerName, comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(newRating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Get all ratings for a book
     */
    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<Rating>> getBookRatings(@PathVariable Long id) {
        List<Rating> ratings = ratingService.getRatingsByBookId(id);
        return ResponseEntity.ok(ratings);
    }
    
    /**
     * Get average rating for a book
     */
    @GetMapping("/{id}/ratings/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        Double averageRating = ratingService.getAverageRating(id);
        return ResponseEntity.ok(averageRating);
    }
    
    /**
     * Get rating count for a book
     */
    @GetMapping("/{id}/ratings/count")
    public ResponseEntity<Long> getRatingCount(@PathVariable Long id) {
        long count = ratingService.getRatingCount(id);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Update a rating
     */
    @PutMapping("/ratings/{ratingId}")
    public ResponseEntity<?> updateRating(@PathVariable Long ratingId, @RequestBody Map<String, Object> ratingData) {
        try {
            Integer rating = (Integer) ratingData.get("rating");
            String comment = (String) ratingData.get("comment");
            
            if (rating == null || rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
            }
            
            Rating updatedRating = ratingService.updateRating(ratingId, rating, comment);
            return ResponseEntity.ok(updatedRating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Delete a rating
     */
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId) {
        try {
            ratingService.deleteRating(ratingId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get recent ratings across all books
     */
    @GetMapping("/ratings/recent")
    public ResponseEntity<List<Rating>> getRecentRatings() {
        List<Rating> recentRatings = ratingService.getRecentRatings();
        return ResponseEntity.ok(recentRatings);
    }
}
