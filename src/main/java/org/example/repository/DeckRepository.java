package org.example.repository;

import org.example.model.Deck;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    List<Deck> findByUserOrderByCreatedAtDesc(User user);

    List<Deck> findByBookId(Long bookId);
}
