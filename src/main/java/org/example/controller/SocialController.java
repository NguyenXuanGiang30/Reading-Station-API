package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.ActivityDTO;
import org.example.dto.CommentDTO;
import org.example.dto.CreateCommentRequest;
import org.example.dto.FriendDTO;
import org.example.security.UserPrincipal;
import org.example.service.SocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
@Tag(name = "Social", description = "API mạng xã hội - bạn bè và hoạt động")
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    // ==================== FRIENDS ====================

    @GetMapping("/friends")
    @Operation(summary = "Lấy danh sách bạn bè")
    public ResponseEntity<List<FriendDTO>> getFriends(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.getFriends(user.getId()));
    }

    @GetMapping("/friends/requests")
    @Operation(summary = "Lấy danh sách lời mời kết bạn đang chờ")
    public ResponseEntity<List<FriendDTO>> getPendingRequests(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.getPendingRequests(user.getId()));
    }

    @PostMapping("/friends/request")
    @Operation(summary = "Gửi lời mời kết bạn")
    public ResponseEntity<FriendDTO> sendFriendRequest(
            @RequestBody Map<String, Long> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.sendFriendRequest(body.get("friendId"), user.getId()));
    }

    @PostMapping("/friends/{friendshipId}/accept")
    @Operation(summary = "Chấp nhận lời mời kết bạn")
    public ResponseEntity<FriendDTO> acceptFriendRequest(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.acceptFriendRequest(friendshipId, user.getId()));
    }

    @PostMapping("/friends/{friendshipId}/reject")
    @Operation(summary = "Từ chối lời mời kết bạn")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        socialService.rejectFriendRequest(friendshipId, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/friends/{friendId}")
    @Operation(summary = "Xóa bạn bè")
    public ResponseEntity<Void> removeFriend(
            @PathVariable Long friendId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        socialService.removeFriend(friendId, user.getId());
        return ResponseEntity.ok().build();
    }

    // ==================== ACTIVITY FEED ====================

    @GetMapping("/feed")
    @Operation(summary = "Lấy hoạt động của bạn bè (Social Feed)")
    public ResponseEntity<List<ActivityDTO>> getFeed(
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.getFriendActivities(user.getId(), limit));
    }

    @GetMapping("/activities")
    @Operation(summary = "Lấy hoạt động của bản thân")
    public ResponseEntity<List<ActivityDTO>> getMyActivities(
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.getUserActivities(user.getId(), limit));
    }

    // ==================== FRIEND PROFILE ====================

    @GetMapping("/users/{userId}/profile")
    @Operation(summary = "Xem profile của người dùng khác")
    public ResponseEntity<FriendDTO> getUserProfile(
            @PathVariable Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.getFriendProfile(userId, user.getId()));
    }

    @GetMapping("/users/{userId}/activities")
    @Operation(summary = "Lấy hoạt động của người dùng khác")
    public ResponseEntity<List<ActivityDTO>> getUserActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.getUserActivities(userId, limit));
    }

    // ==================== LIKES ====================

    @PostMapping("/activities/{activityId}/like")
    @Operation(summary = "Thích một hoạt động")
    public ResponseEntity<ActivityDTO> likeActivity(
            @PathVariable Long activityId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.likeActivity(activityId, user.getId()));
    }

    @DeleteMapping("/activities/{activityId}/like")
    @Operation(summary = "Bỏ thích một hoạt động")
    public ResponseEntity<ActivityDTO> unlikeActivity(
            @PathVariable Long activityId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.unlikeActivity(activityId, user.getId()));
    }

    @PostMapping("/activities/{activityId}/toggle-like")
    @Operation(summary = "Toggle thích/bỏ thích một hoạt động")
    public ResponseEntity<ActivityDTO> toggleLike(
            @PathVariable Long activityId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.toggleLike(activityId, user.getId()));
    }

    // ==================== COMMENTS ====================

    @GetMapping("/activities/{activityId}/comments")
    @Operation(summary = "Lấy danh sách bình luận của hoạt động")
    public ResponseEntity<List<CommentDTO>> getActivityComments(
            @PathVariable Long activityId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.getActivityComments(activityId, user.getId()));
    }

    @PostMapping("/activities/{activityId}/comments")
    @Operation(summary = "Thêm bình luận vào hoạt động")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long activityId,
            @Valid @RequestBody CreateCommentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.createComment(activityId, request, user.getId()));
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "Cập nhật bình luận")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CreateCommentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(socialService.updateComment(commentId, request, user.getId()));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Xóa bình luận")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        socialService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }
}
