package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    /**
     * Get all books
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * Get book by ID
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    /**
     * Get book by ISBN
     */
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    /**
     * Create a new book
     */
    public Book createBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }
    
    /**
     * Update an existing book
     */
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        // Check if ISBN is being changed and if it already exists
        if (!book.getIsbn().equals(bookDetails.getIsbn()) && 
            bookRepository.existsByIsbn(bookDetails.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + bookDetails.getIsbn() + " already exists");
        }
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPrice(bookDetails.getPrice());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setDescription(bookDetails.getDescription());
        book.setStockQuantity(bookDetails.getStockQuantity());
        
        return bookRepository.save(book);
    }
    
    /**
     * Delete a book
     */
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        bookRepository.delete(book);
    }
    
    /**
     * Search books by author or title
     */
    public List<Book> searchBooks(String searchTerm) {
        return bookRepository.findByAuthorOrTitleContainingIgnoreCase(searchTerm);
    }
    
    /**
     * Get books by author
     */
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    /**
     * Get books by title
     */
    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Get books in stock
     */
    public List<Book> getBooksInStock() {
        return bookRepository.findByStockQuantityGreaterThan(0);
    }
    
    /**
     * Update stock quantity
     */
    public Book updateStock(Long id, Integer quantity) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        book.setStockQuantity(quantity);
        return bookRepository.save(book);
    }
}
