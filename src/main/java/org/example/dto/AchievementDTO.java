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
public class AchievementDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String iconUrl;
    private Integer targetValue;
    private String type;
    private Boolean isUnlocked;
    private LocalDateTime earnedAt;
}
