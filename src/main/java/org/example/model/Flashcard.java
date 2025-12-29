package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho flashcard với SM-2 algorithm
 */
@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    // SM-2 Algorithm fields
    @Column(name = "interval_days", columnDefinition = "INT DEFAULT 1")
    private Integer interval = 1;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer repetition = 0;

    @Column(name = "ease_factor", columnDefinition = "DOUBLE DEFAULT 2.5")
    private Double easeFactor = 2.5;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        nextReviewDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if card is due for review today
     */
    public boolean isDue() {
        return nextReviewDate == null || !nextReviewDate.isAfter(LocalDate.now());
    }

    /**
     * SM-2 Algorithm: Process review quality
     * 
     * @param quality 0=Quên, 3=Nhớ, 5=Thuộc
     */
    public void processReview(int quality) {
        if (quality < 3) {
            // Quên - reset
            repetition = 0;
            interval = 1;
        } else {
            // Nhớ hoặc Thuộc
            if (repetition == 0) {
                interval = 1;
            } else if (repetition == 1) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * easeFactor);
            }
            repetition++;
        }

        // Update ease factor
        easeFactor = easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        if (easeFactor < 1.3)
            easeFactor = 1.3;

        // Set next review date
        nextReviewDate = LocalDate.now().plusDays(interval);
    }
}
