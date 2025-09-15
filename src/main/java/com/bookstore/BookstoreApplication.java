package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bookstore Application - A simple REST API for managing books with rating feature
 * 
 * @author Suresh Gaikwad
 * @version 1.0.3
 * Build Date: 2025-09-15
 */
@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
        System.out.println("=================================================");
        System.out.println("Bookstore Application Started Successfully!");
        System.out.println("Author: Suresh Gaikwad");
        System.out.println("Version: 1.0.0");
        System.out.println("=================================================");
        System.out.println("API Endpoints: http://localhost:8080/api/books");
        System.out.println("Health Check: http://localhost:8080/actuator/health");
        System.out.println("Application Info: http://localhost:8080/actuator/info");
        if (System.getProperty("spring.profiles.active") == null || 
            !System.getProperty("spring.profiles.active").contains("kubernetes")) {
            System.out.println("H2 Database Console: http://localhost:8080/h2-console");
        }
        System.out.println("=================================================");
    }
}
