package org.example.repository;

import org.example.model.Book;
import org.example.model.Note;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserOrderByCreatedAtDesc(User user);

    List<Note> findByBookOrderByPageNumberAsc(Book book);

    List<Note> findByUserAndContentContainingIgnoreCase(User user, String content);

    long countByUser(User user);
}
