package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Author is required")
    @Column(nullable = false)
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Column(nullable = false, unique = true)
    private String isbn;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "publication_year")
    private Integer publicationYear;
    
    private String description;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rating> ratings = new ArrayList<>();
    
    // Constructors
    public Book() {}
    
    public Book(String title, String author, String isbn, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Rating> getRatings() {
        return ratings;
    }
    
    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
    
    // Helper methods for rating calculations
    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        
        double sum = ratings.stream()
                .mapToInt(Rating::getRating)
                .sum();
        
        return BigDecimal.valueOf(sum / ratings.size())
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    public int getRatingCount() {
        return ratings != null ? ratings.size() : 0;
    }
    
    public String getStarRating() {
        Double avgRating = getAverageRating();
        if (avgRating == 0.0) {
            return "☆☆☆☆☆";
        }
        
        StringBuilder stars = new StringBuilder();
        int fullStars = avgRating.intValue();
        boolean hasHalfStar = (avgRating - fullStars) >= 0.5;
        
        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        // Add half star if needed
        if (hasHalfStar && fullStars < 5) {
            stars.append("☆");
            fullStars++;
        }
        
        // Add empty stars
        for (int i = fullStars; i < 5; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", price=" + price +
                ", publicationYear=" + publicationYear +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
