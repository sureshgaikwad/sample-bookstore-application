package com.bookstore.config;

import com.bookstore.model.Book;
import com.bookstore.model.Rating;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Initializer - Loads sample data into the database
 * 
 * @author Suresh Gaikwad
 * @version 1.0.0
 */

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (bookRepository.count() == 0) {
            initializeBooks();
            initializeRatings();
        }
    }

    private void initializeBooks() {
        // Create sample books
        Book book1 = new Book();
        book1.setTitle("The Great Gatsby");
        book1.setAuthor("F. Scott Fitzgerald");
        book1.setIsbn("978-0-7432-7356-5");
        book1.setPrice(new BigDecimal("12.99"));
        book1.setPublicationYear(1925);
        book1.setDescription("A classic American novel set in the Jazz Age");
        book1.setStockQuantity(25);

        Book book2 = new Book();
        book2.setTitle("To Kill a Mockingbird");
        book2.setAuthor("Harper Lee");
        book2.setIsbn("978-0-06-112008-4");
        book2.setPrice(new BigDecimal("14.99"));
        book2.setPublicationYear(1960);
        book2.setDescription("A gripping tale of racial injustice and childhood innocence");
        book2.setStockQuantity(30);

        Book book3 = new Book();
        book3.setTitle("1984");
        book3.setAuthor("George Orwell");
        book3.setIsbn("978-0-452-28423-4");
        book3.setPrice(new BigDecimal("13.99"));
        book3.setPublicationYear(1949);
        book3.setDescription("A dystopian social science fiction novel");
        book3.setStockQuantity(20);

        Book book4 = new Book();
        book4.setTitle("Pride and Prejudice");
        book4.setAuthor("Jane Austen");
        book4.setIsbn("978-0-14-143951-8");
        book4.setPrice(new BigDecimal("11.99"));
        book4.setPublicationYear(1813);
        book4.setDescription("A romantic novel of manners");
        book4.setStockQuantity(15);

        Book book5 = new Book();
        book5.setTitle("The Catcher in the Rye");
        book5.setAuthor("J.D. Salinger");
        book5.setIsbn("978-0-316-76948-0");
        book5.setPrice(new BigDecimal("13.50"));
        book5.setPublicationYear(1951);
        book5.setDescription("A controversial novel about teenage rebellion");
        book5.setStockQuantity(18);

        // Save all books
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);

        System.out.println("Sample books initialized successfully!");
    }
    
    private void initializeRatings() {
        // Get all books
        List<Book> books = bookRepository.findAll();
        
        if (books.size() >= 5) {
            Book book1 = books.get(0); // The Great Gatsby
            Book book2 = books.get(1); // To Kill a Mockingbird
            Book book3 = books.get(2); // 1984
            Book book4 = books.get(3); // Pride and Prejudice
            Book book5 = books.get(4); // The Catcher in the Rye
            
            // Ratings for "The Great Gatsby"
            ratingRepository.save(new Rating(5, "Alice Johnson", "A masterpiece of American literature! The symbolism and prose are simply beautiful.", book1));
            ratingRepository.save(new Rating(4, "Bob Smith", "Great book, but the ending was a bit sad for my taste.", book1));
            ratingRepository.save(new Rating(5, "Carol Davis", "One of my all-time favorites. Fitzgerald's writing is incredible.", book1));
            
            // Ratings for "To Kill a Mockingbird"
            ratingRepository.save(new Rating(5, "David Wilson", "Powerful and moving. A must-read for everyone.", book2));
            ratingRepository.save(new Rating(5, "Emma Brown", "Beautifully written with important themes that are still relevant today.", book2));
            ratingRepository.save(new Rating(4, "Frank Miller", "Excellent story and character development.", book2));
            ratingRepository.save(new Rating(5, "Grace Lee", "This book changed my perspective on many things. Highly recommended!", book2));
            
            // Ratings for "1984"
            ratingRepository.save(new Rating(5, "Henry Jones", "Chilling and prophetic. More relevant than ever in today's world.", book3));
            ratingRepository.save(new Rating(4, "Ivy Chen", "Dystopian masterpiece, though quite depressing.", book3));
            ratingRepository.save(new Rating(5, "Jack Taylor", "Orwell's vision is terrifyingly accurate.", book3));
            
            // Ratings for "Pride and Prejudice"
            ratingRepository.save(new Rating(5, "Kate Anderson", "Perfect romance with witty dialogue and strong characters.", book4));
            ratingRepository.save(new Rating(4, "Liam O'Connor", "Not usually my genre, but I enjoyed it more than expected.", book4));
            ratingRepository.save(new Rating(5, "Mary White", "Jane Austen at her finest. Elizabeth Bennet is such a great character!", book4));
            ratingRepository.save(new Rating(4, "Nick Garcia", "Well-written period piece with timeless themes.", book4));
            
            // Ratings for "The Catcher in the Rye"
            ratingRepository.save(new Rating(3, "Olivia Martinez", "Interesting but Holden can be quite annoying at times.", book5));
            ratingRepository.save(new Rating(4, "Paul Kim", "A unique voice in literature. Captures teenage angst perfectly.", book5));
            ratingRepository.save(new Rating(2, "Quinn Thompson", "Didn't connect with the main character. Found it hard to finish.", book5));
            ratingRepository.save(new Rating(5, "Rachel Green", "Brilliant portrayal of adolescent alienation. Salinger is a genius.", book5));
            
            System.out.println("Sample ratings initialized successfully!");
        }
    }
}
