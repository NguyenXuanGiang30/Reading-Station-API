package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity lưu phiên đọc sách (Focus Mode)
 */
@Entity
@Table(name = "reading_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "start_page")
    private Integer startPage;

    @Column(name = "end_page")
    private Integer endPage;

    @Column(name = "pages_read")
    private Integer pagesRead;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "session_date")
    private LocalDate sessionDate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (sessionDate == null) {
            sessionDate = LocalDate.now();
        }
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }
}
