package com.bookstore.config;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (bookRepository.count() == 0) {
            initializeBooks();
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
}
