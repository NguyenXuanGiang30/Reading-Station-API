package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingSessionDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Integer startPage;
    private Integer endPage;
    private Integer pagesRead;
    private Integer durationMinutes;
    private String notes;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
