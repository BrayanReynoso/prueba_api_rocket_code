package com.rocket.rocket.controller;

import com.rocket.rocket.model.Book;
import com.rocket.rocket.service.BookService;
import com.rocket.rocket.utils.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("${API-URL}/books")
@RestController
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Obtener todos los libros
    @GetMapping("/all")
    public ResponseEntity<CustomResponse<List<Book>>> getAllBooks() {
        CustomResponse<List<Book>> response = bookService.findAll();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Obtener solo libros disponibles
    @GetMapping("/available")
    public ResponseEntity<CustomResponse<List<Book>>> getAvailableBooks() {
        CustomResponse<List<Book>> response = bookService.findAvailable();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Buscar libros por título
    @GetMapping("/search/title")
    public ResponseEntity<CustomResponse<List<Book>>> searchBooksByTitle(@RequestParam String title) {
        CustomResponse<List<Book>> response = bookService.findByTitle(title);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Buscar libros por autor
    @GetMapping("/search/author")
    public ResponseEntity<CustomResponse<List<Book>>> searchBooksByAuthor(@RequestParam String author) {
        CustomResponse<List<Book>> response = bookService.findByAuthor(author);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Obtener un libro por ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Book>> getBookById(@PathVariable Long id) {
        CustomResponse<Book> response = bookService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Crear un nuevo libro
    @PostMapping("/")
    public ResponseEntity<CustomResponse<Book>> createBook(@RequestBody Book book) {
        CustomResponse<Book> response = bookService.save(book);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Actualizar un libro
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<Book>> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        CustomResponse<Book> response = bookService.update(book);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<CustomResponse<Book>> changeBookAvailability(@PathVariable Long id) {
        CustomResponse<Book> response = bookService.toggleAvailability(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Marcar un libro como no disponible (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<String>> deleteBook(@PathVariable Long id) {
        CustomResponse<String> response = bookService.delete(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
    // Actualizar el stock de un libro
    @PutMapping("/{id}/stock")
    public ResponseEntity<CustomResponse<Void>> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        CustomResponse<Void> response = bookService.updateStock(id, stock);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}