package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho bộ flashcard (deck)
 */
@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String color;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL)
    private List<Flashcard> flashcards = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Helper methods
    public int getTotalCards() {
        return flashcards.size();
    }

    public long getDueCardsCount() {
        return flashcards.stream()
                .filter(Flashcard::isDue)
                .count();
    }

    public int getMasteredPercentage() {
        if (flashcards.isEmpty())
            return 0;
        long mastered = flashcards.stream()
                .filter(f -> f.getRepetition() >= 5)
                .count();
        return (int) ((mastered * 100.0) / flashcards.size());
    }
}
