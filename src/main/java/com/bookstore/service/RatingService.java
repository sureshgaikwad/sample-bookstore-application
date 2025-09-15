package com.bookstore.service;

import com.bookstore.model.Rating;
import com.bookstore.model.Book;
import com.bookstore.repository.RatingRepository;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RatingService {
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    /**
     * Add a new rating for a book
     */
    public Rating addRating(Long bookId, Integer rating, String reviewerName, String comment) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new IllegalArgumentException("Book not found with id: " + bookId);
        }
        
        // Check if reviewer has already rated this book
        if (ratingRepository.existsByBookIdAndReviewerNameIgnoreCase(bookId, reviewerName)) {
            throw new IllegalArgumentException("You have already rated this book");
        }
        
        Rating newRating = new Rating();
        newRating.setRating(rating);
        newRating.setReviewerName(reviewerName);
        newRating.setComment(comment);
        newRating.setBook(book.get());
        
        return ratingRepository.save(newRating);
    }
    
    /**
     * Get all ratings for a book
     */
    @Transactional(readOnly = true)
    public List<Rating> getRatingsByBookId(Long bookId) {
        return ratingRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }
    
    /**
     * Get average rating for a book
     */
    @Transactional(readOnly = true)
    public Double getAverageRating(Long bookId) {
        Double average = ratingRepository.findAverageRatingByBookId(bookId);
        return average != null ? average : 0.0;
    }
    
    /**
     * Get total rating count for a book
     */
    @Transactional(readOnly = true)
    public long getRatingCount(Long bookId) {
        return ratingRepository.countByBookId(bookId);
    }
    
    /**
     * Get recent ratings across all books
     */
    @Transactional(readOnly = true)
    public List<Rating> getRecentRatings() {
        return ratingRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    /**
     * Update an existing rating
     */
    public Rating updateRating(Long ratingId, Integer rating, String comment) {
        Optional<Rating> existingRating = ratingRepository.findById(ratingId);
        if (existingRating.isEmpty()) {
            throw new IllegalArgumentException("Rating not found with id: " + ratingId);
        }
        
        Rating ratingToUpdate = existingRating.get();
        ratingToUpdate.setRating(rating);
        ratingToUpdate.setComment(comment);
        
        return ratingRepository.save(ratingToUpdate);
    }
    
    /**
     * Delete a rating
     */
    public void deleteRating(Long ratingId) {
        if (!ratingRepository.existsById(ratingId)) {
            throw new IllegalArgumentException("Rating not found with id: " + ratingId);
        }
        ratingRepository.deleteById(ratingId);
    }
    
    /**
     * Check if a reviewer has already rated a book
     */
    @Transactional(readOnly = true)
    public boolean hasUserRatedBook(Long bookId, String reviewerName) {
        return ratingRepository.existsByBookIdAndReviewerNameIgnoreCase(bookId, reviewerName);
    }
    
    /**
     * Get all ratings
     */
    @Transactional(readOnly = true)
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
}
