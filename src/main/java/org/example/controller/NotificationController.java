package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.NotificationSettingDTO;
import org.example.dto.UpdateNotificationSettingsRequest;
import org.example.security.UserPrincipal;
import org.example.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/settings")
@Tag(name = "Notifications", description = "API cài đặt thông báo")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Lấy cài đặt thông báo")
    public ResponseEntity<NotificationSettingDTO> getSettings(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(notificationService.getSettings(user.getId()));
    }

    @PutMapping
    @Operation(summary = "Cập nhật cài đặt thông báo")
    public ResponseEntity<NotificationSettingDTO> updateSettings(
            @RequestBody UpdateNotificationSettingsRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(notificationService.updateSettings(user.getId(), request));
    }
}
