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
public class ActivityDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userAvatarUrl;
    private String activityType; // BOOK_ADDED, BOOK_COMPLETED, NOTE_CREATED, REVIEW_COMPLETED
    private String description;
    private Long bookId;
    private String bookTitle;
    private String bookCoverUrl;
    private LocalDateTime createdAt;
    private Integer likesCount;
    private Integer commentsCount;
    private boolean isLiked; // Người dùng hiện tại đã like chưa
}
