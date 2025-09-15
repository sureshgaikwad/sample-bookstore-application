package com.bookstore.repository;

import com.bookstore.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    /**
     * Find all ratings for a specific book
     */
    List<Rating> findByBookIdOrderByCreatedAtDesc(Long bookId);
    
    /**
     * Count total ratings for a specific book
     */
    long countByBookId(Long bookId);
    
    /**
     * Calculate average rating for a specific book
     */
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
    
    /**
     * Find ratings by reviewer name
     */
    List<Rating> findByReviewerNameIgnoreCase(String reviewerName);
    
    /**
     * Find ratings by rating value
     */
    List<Rating> findByRating(Integer rating);
    
    /**
     * Find recent ratings across all books (for homepage/dashboard)
     */
    List<Rating> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * Check if a reviewer has already rated a book (to prevent duplicate ratings)
     */
    boolean existsByBookIdAndReviewerNameIgnoreCase(Long bookId, String reviewerName);
}
