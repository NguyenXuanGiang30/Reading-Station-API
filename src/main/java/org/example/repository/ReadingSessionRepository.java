package org.example.repository;

import org.example.model.Book;
import org.example.model.ReadingSession;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {

    List<ReadingSession> findByUserOrderByCreatedAtDesc(User user);

    List<ReadingSession> findByUserOrderByStartedAtDesc(User user);

    List<ReadingSession> findByBookOrderByStartedAtDesc(Book book);

    List<ReadingSession> findByBookIdOrderBySessionDateDesc(Long bookId);

    @Query("SELECT rs FROM ReadingSession rs WHERE rs.user = :user AND rs.startedAt BETWEEN :start AND :end ORDER BY rs.startedAt DESC")
    List<ReadingSession> findByUserAndStartedAtBetween(@Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(rs.durationMinutes), 0) FROM ReadingSession rs WHERE rs.user = :user")
    long getTotalReadingMinutes(@Param("user") User user);
}
