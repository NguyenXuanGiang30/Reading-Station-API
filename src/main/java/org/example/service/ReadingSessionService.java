package org.example.service;

import org.example.dto.ReadingSessionDTO;
import org.example.model.Book;
import org.example.model.ReadingSession;
import org.example.model.User;
import org.example.model.enums.BookStatus;
import org.example.repository.BookRepository;
import org.example.repository.ReadingSessionRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadingSessionService {

    private final ReadingSessionRepository sessionRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReadingSessionService(ReadingSessionRepository sessionRepository,
            BookRepository bookRepository,
            UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<ReadingSessionDTO> getBookSessions(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return sessionRepository.findByBookOrderByStartedAtDesc(book).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReadingSessionDTO> getUserSessions(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ReadingSession> sessions;
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            sessions = sessionRepository.findByUserAndStartedAtBetween(user, startOfDay, endOfDay);
        } else {
            sessions = sessionRepository.findByUserOrderByStartedAtDesc(user);
        }

        return sessions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ReadingSessionDTO startSession(Long bookId, Integer startPage, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ReadingSession session = ReadingSession.builder()
                .user(user)
                .book(book)
                .startPage(startPage != null ? startPage : book.getCurrentPage())
                .startedAt(LocalDateTime.now())
                .build();

        // Update book status if needed
        if (book.getStatus() == BookStatus.WANT_TO_READ) {
            book.setStatus(BookStatus.READING);
            book.setStartedAt(LocalDate.now());
            bookRepository.save(book);
        }

        sessionRepository.save(session);
        return toDTO(session);
    }

    @Transactional
    public ReadingSessionDTO endSession(Long sessionId, Integer endPage, String notes, Long userId) {
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        session.setEndPage(endPage);
        session.setEndedAt(LocalDateTime.now());
        session.setNotes(notes);

        // Calculate duration
        if (session.getStartedAt() != null && session.getEndedAt() != null) {
            long minutes = java.time.Duration.between(session.getStartedAt(), session.getEndedAt()).toMinutes();
            session.setDurationMinutes((int) minutes);
        }

        // Calculate pages read
        if (session.getStartPage() != null && endPage != null) {
            session.setPagesRead(endPage - session.getStartPage());
        }

        sessionRepository.save(session);

        // Update book progress
        Book book = session.getBook();
        if (endPage != null && endPage > book.getCurrentPage()) {
            book.setCurrentPage(endPage);
            if (book.getTotalPages() != null && endPage >= book.getTotalPages()) {
                book.setStatus(BookStatus.COMPLETED);
                book.setFinishedAt(LocalDate.now());
            }
            bookRepository.save(book);
        }

        // Update user streak
        updateUserStreak(session.getUser());

        return toDTO(session);
    }

    public ReadingSessionDTO getSession(Long sessionId, Long userId) {
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return toDTO(session);
    }

    private void updateUserStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastActivity = user.getLastActivityDate();

        if (lastActivity == null || lastActivity.isBefore(today.minusDays(1))) {
            user.setStreak(1);
        } else if (lastActivity.equals(today.minusDays(1))) {
            user.setStreak(user.getStreak() + 1);
        }
        // If same day, streak doesn't change

        user.setLastActivityDate(today);
        userRepository.save(user);
    }

    private ReadingSessionDTO toDTO(ReadingSession session) {
        return ReadingSessionDTO.builder()
                .id(session.getId())
                .bookId(session.getBook().getId())
                .bookTitle(session.getBook().getTitle())
                .startPage(session.getStartPage())
                .endPage(session.getEndPage())
                .pagesRead(session.getPagesRead())
                .durationMinutes(session.getDurationMinutes())
                .notes(session.getNotes())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .build();
    }
}
