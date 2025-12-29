package org.example.repository;

import org.example.model.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    List<BookReview> findByBookIdOrderByCreatedAtDesc(Long bookId);

    Optional<BookReview> findByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT br FROM BookReview br WHERE br.book.id = :bookId AND br.user.id IN :friendIds")
    List<BookReview> findFriendReviews(@Param("bookId") Long bookId, @Param("friendIds") List<Long> friendIds);
}
