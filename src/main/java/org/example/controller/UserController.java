package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.UserDTO;
import org.example.model.User;
import org.example.repository.*;
import org.example.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API quản lý người dùng")
public class UserController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NoteRepository noteRepository;
    private final FlashcardRepository flashcardRepository;

    public UserController(UserRepository userRepository, BookRepository bookRepository,
            NoteRepository noteRepository, FlashcardRepository flashcardRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.noteRepository = noteRepository;
        this.flashcardRepository = flashcardRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin người dùng hiện tại")
    public ResponseEntity<UserDTO> getCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .streak(user.getStreak())
                .booksRead(bookRepository.countCompletedBooks(user))
                .totalNotes(noteRepository.countByUser(user))
                .totalFlashcards(flashcardRepository.countByUserId(user.getId()))
                .build();

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    @Operation(summary = "Cập nhật thông tin người dùng")
    public ResponseEntity<UserDTO> updateProfile(
            @RequestBody Map<String, String> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (body.containsKey("fullName"))
            user.setFullName(body.get("fullName"));
        if (body.containsKey("bio"))
            user.setBio(body.get("bio"));
        if (body.containsKey("avatarUrl"))
            user.setAvatarUrl(body.get("avatarUrl"));

        userRepository.save(user);

        return getCurrentUser(principal);
    }

    @GetMapping("/me/stats")
    @Operation(summary = "Lấy thống kê đọc sách")
    public ResponseEntity<Map<String, Object>> getStats(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("booksRead", bookRepository.countCompletedBooks(user));
        stats.put("totalPagesRead", bookRepository.getTotalPagesRead(user));
        stats.put("totalNotes", noteRepository.countByUser(user));
        stats.put("totalFlashcards", flashcardRepository.countByUserId(user.getId()));
        stats.put("masteredCards", flashcardRepository.countMasteredByUserId(user.getId()));
        stats.put("dueCardsToday", flashcardRepository.countDueCards(user.getId(), LocalDate.now()));
        stats.put("streak", user.getStreak());

        return ResponseEntity.ok(stats);
    }
}
