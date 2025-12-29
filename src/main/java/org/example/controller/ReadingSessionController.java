package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ReadingSessionDTO;
import org.example.security.UserPrincipal;
import org.example.service.ReadingSessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Reading Sessions", description = "API quản lý phiên đọc sách (Focus Mode)")
public class ReadingSessionController {

    private final ReadingSessionService sessionService;

    public ReadingSessionController(ReadingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/books/{bookId}/sessions")
    @Operation(summary = "Lấy lịch sử phiên đọc của sách")
    public ResponseEntity<List<ReadingSessionDTO>> getBookSessions(
            @PathVariable Long bookId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(sessionService.getBookSessions(bookId, user.getId()));
    }

    @GetMapping("/reading-sessions")
    @Operation(summary = "Lấy lịch sử phiên đọc của người dùng")
    public ResponseEntity<List<ReadingSessionDTO>> getUserSessions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(sessionService.getUserSessions(user.getId(), date));
    }

    @GetMapping("/reading-sessions/{id}")
    @Operation(summary = "Lấy chi tiết phiên đọc")
    public ResponseEntity<ReadingSessionDTO> getSession(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(sessionService.getSession(id, user.getId()));
    }

    @PostMapping("/books/{bookId}/sessions/start")
    @Operation(summary = "Bắt đầu phiên đọc mới (Focus Mode)")
    public ResponseEntity<ReadingSessionDTO> startSession(
            @PathVariable Long bookId,
            @RequestBody(required = false) Map<String, Integer> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        Integer startPage = body != null ? body.get("startPage") : null;
        return ResponseEntity.ok(sessionService.startSession(bookId, startPage, user.getId()));
    }

    @PostMapping("/reading-sessions/{id}/end")
    @Operation(summary = "Kết thúc phiên đọc")
    public ResponseEntity<ReadingSessionDTO> endSession(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        Integer endPage = body.get("endPage") != null ? Integer.valueOf(body.get("endPage").toString()) : null;
        String notes = (String) body.get("notes");
        return ResponseEntity.ok(sessionService.endSession(id, endPage, notes, user.getId()));
    }
}
