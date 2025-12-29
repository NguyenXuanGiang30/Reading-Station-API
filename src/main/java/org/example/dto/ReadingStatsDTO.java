package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingStatsDTO {
    // Tổng quan
    private Long totalBooksRead;
    private Long totalPagesRead;
    private Long totalNotes;
    private Long readingStreak;

    // Biểu đồ theo tháng (6 tháng gần nhất)
    private List<MonthlyStat> monthlyStats;

    // Biểu đồ theo thể loại (Reading DNA)
    private Map<String, Long> booksByCategory;

    @Data
    @AllArgsConstructor
    public static class MonthlyStat {
        private String month; // e.g., "2023-10"
        private Long booksRead;
    }
}
