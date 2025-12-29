package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.googlebooks.GoogleBookDTO;
import org.example.service.GoogleBooksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/google-books")
@Tag(name = "Google Books", description = "API tìm kiếm sách từ Google Books")
public class GoogleBooksController {

    private final GoogleBooksService googleBooksService;

    public GoogleBooksController(GoogleBooksService googleBooksService) {
        this.googleBooksService = googleBooksService;
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm sách từ Google Books")
    public ResponseEntity<List<GoogleBookDTO>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults) {
        return ResponseEntity.ok(googleBooksService.searchBooks(query, maxResults));
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Tìm sách theo ISBN")
    public ResponseEntity<GoogleBookDTO> searchByIsbn(@PathVariable String isbn) {
        GoogleBookDTO book = googleBooksService.searchByIsbn(isbn);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @GetMapping("/{googleBookId}")
    @Operation(summary = "Lấy chi tiết sách từ Google Books")
    public ResponseEntity<GoogleBookDTO> getBookById(@PathVariable String googleBookId) {
        GoogleBookDTO book = googleBooksService.getBookById(googleBookId);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }
}
