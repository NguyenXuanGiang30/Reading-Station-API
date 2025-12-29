package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho hoạt động trong social feed
 */
@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "likes_count", columnDefinition = "INT DEFAULT 0")
    private Integer likesCount = 0;

    @Column(name = "comments_count", columnDefinition = "INT DEFAULT 0")
    private Integer commentsCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
