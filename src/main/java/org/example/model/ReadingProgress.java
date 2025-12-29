package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity lưu lịch sử cập nhật tiến độ đọc
 */
@Entity
@Table(name = "reading_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 500)
    private String note;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
}
