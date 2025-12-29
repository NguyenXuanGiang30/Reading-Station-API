package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ReadingStatsDTO;
import org.example.security.UserPrincipal;
import org.example.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "API thống kê đọc sách (biểu đồ)")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    @Operation(summary = "Lấy dữ liệu thống kê tổng quan và biểu đồ")
    public ResponseEntity<ReadingStatsDTO> getStats(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(statisticsService.getStats(user.getId()));
    }
}
