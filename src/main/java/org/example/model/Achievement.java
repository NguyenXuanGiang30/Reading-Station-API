package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g. "FIRST_BOOK", "STREAK_7_DAYS"

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "target_value")
    private Integer targetValue; // Giá trị cần đạt được (ví dụ: 10 sách)

    @Column(name = "achievement_type") // READING, STREAK, NOTE, REVIEW
    private String type;
}
