package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.CreateNoteRequest;
import org.example.dto.NoteDTO;
import org.example.security.UserPrincipal;
import org.example.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "API quản lý ghi chú")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách ghi chú của người dùng")
    public ResponseEntity<List<NoteDTO>> getNotes(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(noteService.getUserNotes(user.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết ghi chú")
    public ResponseEntity<NoteDTO> getNote(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(noteService.getNoteById(id, user.getId()));
    }

    @PostMapping
    @Operation(summary = "Tạo ghi chú mới")
    public ResponseEntity<NoteDTO> createNote(
            @Valid @RequestBody CreateNoteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(noteService.createNote(request, user.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật ghi chú")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody CreateNoteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(noteService.updateNote(id, request, user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa ghi chú")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        noteService.deleteNote(id, user.getId());
        return ResponseEntity.ok().build();
    }
}
