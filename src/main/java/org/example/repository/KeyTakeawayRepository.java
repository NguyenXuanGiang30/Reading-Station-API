package org.example.repository;

import org.example.model.Book;
import org.example.model.KeyTakeaway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeyTakeawayRepository extends JpaRepository<KeyTakeaway, Long> {

    List<KeyTakeaway> findByBookOrderByOrderIndexAsc(Book book);

    List<KeyTakeaway> findByBookId(Long bookId);

    @Query("SELECT MAX(kt.orderIndex) FROM KeyTakeaway kt WHERE kt.book = :book")
    Optional<Integer> findMaxOrderIndexByBook(@Param("book") Book book);
}
