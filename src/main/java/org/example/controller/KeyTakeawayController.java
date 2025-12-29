package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.KeyTakeawayDTO;
import org.example.security.UserPrincipal;
import org.example.service.KeyTakeawayService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books/{bookId}/takeaways")
@Tag(name = "Key Takeaways", description = "API quản lý key points của sách")
public class KeyTakeawayController {

    private final KeyTakeawayService takeawayService;

    public KeyTakeawayController(KeyTakeawayService takeawayService) {
        this.takeawayService = takeawayService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách key takeaways của sách")
    public ResponseEntity<List<KeyTakeawayDTO>> getTakeaways(
            @PathVariable Long bookId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(takeawayService.getBookTakeaways(bookId, user.getId()));
    }

    @PostMapping
    @Operation(summary = "Thêm key takeaway mới")
    public ResponseEntity<KeyTakeawayDTO> createTakeaway(
            @PathVariable Long bookId,
            @RequestBody Map<String, Object> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        String content = (String) body.get("content");
        Integer pageNumber = body.get("pageNumber") != null ? Integer.valueOf(body.get("pageNumber").toString()) : null;
        return ResponseEntity.ok(takeawayService.createTakeaway(bookId, content, pageNumber, user.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật key takeaway")
    public ResponseEntity<KeyTakeawayDTO> updateTakeaway(
            @PathVariable Long bookId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        String content = (String) body.get("content");
        Integer pageNumber = body.get("pageNumber") != null ? Integer.valueOf(body.get("pageNumber").toString()) : null;
        return ResponseEntity.ok(takeawayService.updateTakeaway(id, content, pageNumber, user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa key takeaway")
    public ResponseEntity<Void> deleteTakeaway(
            @PathVariable Long bookId,
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        takeawayService.deleteTakeaway(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reorder")
    @Operation(summary = "Sắp xếp lại thứ tự takeaways")
    public ResponseEntity<Void> reorderTakeaways(
            @PathVariable Long bookId,
            @RequestBody Map<String, List<Long>> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        takeawayService.reorderTakeaways(bookId, body.get("orderedIds"), user.getId());
        return ResponseEntity.ok().build();
    }
}
