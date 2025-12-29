package org.example.repository;

import org.example.model.Book;
import org.example.model.User;
import org.example.model.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

        List<Book> findByUserOrderByUpdatedAtDesc(User user);

        List<Book> findByUserAndStatusOrderByUpdatedAtDesc(User user, BookStatus status);

        List<Book> findByUserAndTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        User user, String title, String author);

        @Query("SELECT b FROM Book b WHERE b.user = :user AND " +
                        "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        List<Book> searchBooks(@Param("user") User user, @Param("keyword") String keyword);

        @Query("SELECT COUNT(b) FROM Book b WHERE b.user = :user AND b.status = 'COMPLETED'")
        long countCompletedBooks(@Param("user") User user);

        @Query("SELECT SUM(b.currentPage) FROM Book b WHERE b.user = :user")
        Long getTotalPagesRead(@Param("user") User user);

        List<Book> findByIsbn(String isbn);

        @Query("SELECT b.category, COUNT(b) FROM Book b WHERE b.user.id = :userId AND b.status = 'COMPLETED' GROUP BY b.category")
        List<Object[]> countBooksByCategory(@Param("userId") Long userId);

        @Query(value = "SELECT DATE_FORMAT(updated_at, '%Y-%m') as month, COUNT(*) as count FROM books WHERE user_id = :userId AND status = 'COMPLETED' AND updated_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) GROUP BY month ORDER BY month", nativeQuery = true)
        List<Object[]> countBooksByMonth(@Param("userId") Long userId);
}
