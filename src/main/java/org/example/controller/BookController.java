package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.BookDTO;
import org.example.dto.CreateBookRequest;
import org.example.dto.NoteDTO;
import org.example.model.enums.BookStatus;
import org.example.security.UserPrincipal;
import org.example.service.BookService;
import org.example.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "API quản lý sách")
public class BookController {

    private final BookService bookService;
    private final NoteService noteService;

    public BookController(BookService bookService, NoteService noteService) {
        this.bookService = bookService;
        this.noteService = noteService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách sách của người dùng")
    public ResponseEntity<List<BookDTO>> getBooks(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) BookStatus status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(bookService.getUserBooks(user.getId(), status, search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sách")
    public ResponseEntity<BookDTO> getBook(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(bookService.getBookById(id, user.getId()));
    }

    @GetMapping("/{id}/notes")
    @Operation(summary = "Lấy danh sách ghi chú của sách")
    public ResponseEntity<List<NoteDTO>> getBookNotes(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(noteService.getBookNotes(id, user.getId()));
    }

    @PostMapping
    @Operation(summary = "Thêm sách mới")
    public ResponseEntity<BookDTO> createBook(
            @Valid @RequestBody CreateBookRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(bookService.createBook(request, user.getId()));
    }

    @PostMapping("/isbn/{isbn}")
    @Operation(summary = "Thêm sách bằng mã vạch (ISBN)", description = "Quét mã vạch sách, tự động lấy thông tin từ Google Books và thêm vào thư viện")
    public ResponseEntity<BookDTO> createBookFromIsbn(
            @PathVariable String isbn,
            @RequestParam(required = false) BookStatus status,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(bookService.createBookFromIsbn(isbn, status, user.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin sách")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody CreateBookRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(bookService.updateBook(id, request, user.getId()));
    }

    @PutMapping("/{id}/progress")
    @Operation(summary = "Cập nhật tiến độ đọc")
    public ResponseEntity<BookDTO> updateProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(bookService.updateProgress(id, body.get("currentPage"), user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sách")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        bookService.deleteBook(id, user.getId());
        return ResponseEntity.ok().build();
    }
}
