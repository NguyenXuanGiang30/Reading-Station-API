package org.example.repository;

import org.example.model.Deck;
import org.example.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    List<Flashcard> findByDeck(Deck deck);

    List<Flashcard> findByDeckId(Long deckId);

    @Query("SELECT f FROM Flashcard f WHERE f.deck.user.id = :userId " +
            "AND (f.nextReviewDate IS NULL OR f.nextReviewDate <= :today)")
    List<Flashcard> findDueCards(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(f) FROM Flashcard f WHERE f.deck.user.id = :userId " +
            "AND (f.nextReviewDate IS NULL OR f.nextReviewDate <= :today)")
    long countDueCards(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(f) FROM Flashcard f WHERE f.deck.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Flashcard f WHERE f.deck.user.id = :userId AND f.repetition >= 5")
    long countMasteredByUserId(@Param("userId") Long userId);
}
