package org.example.service;

import org.example.dto.ReadingStatsDTO;
import org.example.model.User;
import org.example.repository.BookRepository;
import org.example.repository.NoteRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final BookRepository bookRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public StatisticsService(BookRepository bookRepository,
            NoteRepository noteRepository,
            UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public ReadingStatsDTO getStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Basic stats
        long completedBooks = bookRepository.countCompletedBooks(user);
        Long totalPages = bookRepository.getTotalPagesRead(user);
        long totalNotes = noteRepository.countByUser(user);

        // Charts data
        List<Object[]> monthlyData = bookRepository.countBooksByMonth(userId);
        List<ReadingStatsDTO.MonthlyStat> monthlyStats = new ArrayList<>();
        for (Object[] row : monthlyData) {
            String month = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            monthlyStats.add(new ReadingStatsDTO.MonthlyStat(month, count));
        }

        List<Object[]> categoryData = bookRepository.countBooksByCategory(userId);
        Map<String, Long> booksByCategory = new HashMap<>();
        for (Object[] row : categoryData) {
            String category = (String) row[0];
            if (category == null || category.isEmpty())
                category = "Uncategorized";
            Long count = ((Number) row[1]).longValue();
            booksByCategory.put(category, count);
        }

        return ReadingStatsDTO.builder()
                .totalBooksRead(completedBooks)
                .totalPagesRead(totalPages != null ? totalPages : 0)
                .totalNotes(totalNotes)
                .readingStreak((long) user.getStreak()) // Cast int to long
                .monthlyStats(monthlyStats)
                .booksByCategory(booksByCategory)
                .build();
    }
}
