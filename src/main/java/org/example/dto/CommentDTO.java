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
public class CommentDTO {
    private Long id;
    private Long activityId;
    private Long userId;
    private String userFullName;
    private String userAvatarUrl;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isOwner; // Người dùng hiện tại có phải chủ comment không
}
