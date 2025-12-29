package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.DeckDTO;
import org.example.dto.FlashcardDTO;
import org.example.dto.ReviewRequest;
import org.example.security.UserPrincipal;
import org.example.service.FlashcardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Flashcards", description = "API quản lý flashcard và ôn tập")
public class FlashcardController {

    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    // ============== DECKS ==============

    @GetMapping("/decks")
    @Operation(summary = "Lấy danh sách deck của người dùng")
    public ResponseEntity<List<DeckDTO>> getDecks(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(flashcardService.getUserDecks(user.getId()));
    }

    @GetMapping("/decks/{id}")
    @Operation(summary = "Lấy chi tiết deck")
    public ResponseEntity<DeckDTO> getDeck(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(flashcardService.getDeckById(id, user.getId()));
    }

    @PostMapping("/decks")
    @Operation(summary = "Tạo deck mới")
    public ResponseEntity<DeckDTO> createDeck(
            @RequestBody Map<String, Object> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        Long bookId = body.get("bookId") != null ? Long.valueOf(body.get("bookId").toString()) : null;
        return ResponseEntity.ok(flashcardService.createDeck(name, description, bookId, user.getId()));
    }

    @GetMapping("/decks/{id}/cards")
    @Operation(summary = "Lấy danh sách flashcard trong deck")
    public ResponseEntity<List<FlashcardDTO>> getDeckCards(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(flashcardService.getDeckCards(id, user.getId()));
    }

    // ============== FLASHCARDS ==============

    @GetMapping("/flashcards/due")
    @Operation(summary = "Lấy danh sách flashcard cần ôn hôm nay")
    public ResponseEntity<List<FlashcardDTO>> getDueCards(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(flashcardService.getDueCards(user.getId()));
    }

    @PostMapping("/flashcards")
    @Operation(summary = "Tạo flashcard mới")
    public ResponseEntity<FlashcardDTO> createFlashcard(
            @RequestBody Map<String, Object> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        Long deckId = Long.valueOf(body.get("deckId").toString());
        String question = (String) body.get("question");
        String answer = (String) body.get("answer");
        return ResponseEntity.ok(flashcardService.createFlashcard(deckId, question, answer, user.getId()));
    }

    @PostMapping("/notes/{noteId}/to-flashcard")
    @Operation(summary = "Chuyển ghi chú thành flashcard")
    public ResponseEntity<FlashcardDTO> convertNoteToFlashcard(
            @PathVariable Long noteId,
            @RequestBody Map<String, Long> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(flashcardService.createFromNote(noteId, body.get("deckId"), user.getId()));
    }

    @PostMapping("/flashcards/{id}/review")
    @Operation(summary = "Ghi nhận kết quả ôn tập (SM-2)")
    public ResponseEntity<FlashcardDTO> reviewFlashcard(
            @PathVariable Long id,
            @RequestBody ReviewRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(flashcardService.reviewFlashcard(id, request, user.getId()));
    }

    @DeleteMapping("/flashcards/{id}")
    @Operation(summary = "Xóa flashcard")
    public ResponseEntity<Void> deleteFlashcard(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        flashcardService.deleteFlashcard(id, user.getId());
        return ResponseEntity.ok().build();
    }
}
