package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.enums.BookStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho sách trong thư viện
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(length = 20)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_pages")
    private Integer totalPages;

    @Column(name = "current_page", columnDefinition = "INT DEFAULT 0")
    private Integer currentPage = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status = BookStatus.WANT_TO_READ;

    @Column(length = 255)
    private String location;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer rating = 0;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(name = "finished_at")
    private LocalDate finishedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<KeyTakeaway> keyTakeaways = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<ReadingProgress> progressHistory = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to calculate progress percentage
    public int getProgressPercentage() {
        if (totalPages == null || totalPages == 0)
            return 0;
        return (int) ((currentPage * 100.0) / totalPages);
    }
}
