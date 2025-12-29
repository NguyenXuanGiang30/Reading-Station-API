package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.BookReviewDTO;
import org.example.dto.CreateReviewRequest;
import org.example.security.UserPrincipal;
import org.example.service.BookReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Reviews", description = "API đánh giá và nhận xét sách")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    public BookReviewController(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @GetMapping("/books/{bookId}/reviews")
    @Operation(summary = "Lấy danh sách đánh giá của một cuốn sách")
    public ResponseEntity<List<BookReviewDTO>> getBookReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookReviewService.getBookReviews(bookId));
    }

    @PostMapping("/books/{bookId}/reviews")
    @Operation(summary = "Thêm đánh giá cho sách")
    public ResponseEntity<BookReviewDTO> addReview(
            @PathVariable Long bookId,
            @Valid @RequestBody CreateReviewRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(bookReviewService.addReview(user.getId(), bookId, request));
    }

    @PutMapping("/reviews/{reviewId}")
    @Operation(summary = "Cập nhật đánh giá")
    public ResponseEntity<BookReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(bookReviewService.updateReview(user.getId(), reviewId, request));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Xóa đánh giá")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        bookReviewService.deleteReview(user.getId(), reviewId);
        return ResponseEntity.ok().build();
    }
}
