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
public class BookReviewDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userFullName;
    private String userAvatarUrl;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
