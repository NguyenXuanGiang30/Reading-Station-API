package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.AchievementDTO;
import org.example.security.UserPrincipal;
import org.example.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@Tag(name = "Achievements", description = "API thành tích")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách thành tích của người dùng")
    public ResponseEntity<List<AchievementDTO>> getAchievements(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId()));
    }
}
