package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * Find book by ISBN
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Find books by author (case-insensitive)
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    /**
     * Find books by title (case-insensitive)
     */
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find books by author or title (case-insensitive)
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> findByAuthorOrTitleContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    /**
     * Find books with stock quantity greater than zero
     */
    List<Book> findByStockQuantityGreaterThan(Integer quantity);
    
    /**
     * Check if book exists by ISBN
     */
    boolean existsByIsbn(String isbn);
}
