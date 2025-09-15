package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Web Controller - Handles web UI requests
 * 
 * @author Suresh Gaikwad
 * @version 1.0.0
 */
@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired
    private BookService bookService;

    /**
     * Home page - displays all books
     */
    @GetMapping("/")
    public String home(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        model.addAttribute("pageTitle", "Bookstore - All Books");
        return "books/list";
    }

    /**
     * Book details page
     */
    @GetMapping("/book/{id}")
    public String bookDetails(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("pageTitle", "Book Details - " + book.get().getTitle());
            return "books/details";
        } else {
            return "redirect:/web/?error=Book not found";
        }
    }

    /**
     * Search books
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        if (q != null && !q.trim().isEmpty()) {
            List<Book> books = bookService.searchBooks(q.trim());
            model.addAttribute("books", books);
            model.addAttribute("searchQuery", q);
            model.addAttribute("pageTitle", "Search Results for: " + q);
        } else {
            List<Book> books = bookService.getAllBooks();
            model.addAttribute("books", books);
            model.addAttribute("pageTitle", "All Books");
        }
        return "books/list";
    }

    /**
     * Books by author
     */
    @GetMapping("/author/{author}")
    public String booksByAuthor(@PathVariable String author, Model model) {
        List<Book> books = bookService.getBooksByAuthor(author);
        model.addAttribute("books", books);
        model.addAttribute("pageTitle", "Books by " + author);
        model.addAttribute("filterType", "author");
        model.addAttribute("filterValue", author);
        return "books/list";
    }

    /**
     * Books in stock
     */
    @GetMapping("/in-stock")
    public String booksInStock(Model model) {
        List<Book> books = bookService.getBooksInStock();
        model.addAttribute("books", books);
        model.addAttribute("pageTitle", "Books in Stock");
        model.addAttribute("filterType", "in-stock");
        return "books/list";
    }

    /**
     * Add new book form
     */
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("pageTitle", "Add New Book");
        model.addAttribute("formAction", "/web/add");
        return "books/form";
    }

    /**
     * Handle add book form submission
     */
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            bookService.createBook(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
            return "redirect:/web/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding book: " + e.getMessage());
            return "redirect:/web/add";
        }
    }

    /**
     * Edit book form
     */
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("pageTitle", "Edit Book - " + book.get().getTitle());
            model.addAttribute("formAction", "/web/edit/" + id);
            return "books/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found!");
            return "redirect:/web/";
        }
    }

    /**
     * Handle edit book form submission
     */
    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            bookService.updateBook(id, book);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
            return "redirect:/web/book/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating book: " + e.getMessage());
            return "redirect:/web/edit/" + id;
        }
    }

    /**
     * Delete book
     */
    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book '" + (book.isPresent() ? book.get().getTitle() : "Unknown") + "' deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/web/";
    }
}
