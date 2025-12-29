package org.example.service;

import org.example.dto.BookReviewDTO;
import org.example.dto.CreateReviewRequest;
import org.example.model.Book;
import org.example.model.BookReview;
import org.example.model.User;
import org.example.repository.BookRepository;
import org.example.repository.BookReviewRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SocialService socialService;

    public BookReviewService(BookReviewRepository bookReviewRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            SocialService socialService) {
        this.bookReviewRepository = bookReviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.socialService = socialService;
    }

    public List<BookReviewDTO> getBookReviews(Long bookId) {
        return bookReviewRepository.findByBookIdOrderByCreatedAtDesc(bookId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookReviewDTO addReview(Long userId, Long bookId, CreateReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Check if review already exists
        if (bookReviewRepository.findByUserIdAndBookId(userId, bookId).isPresent()) {
            throw new RuntimeException("Bạn đã đánh giá sách này rồi");
        }

        BookReview review = BookReview.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        bookReviewRepository.save(review);

        // Log activity
        try {
            socialService.createActivity(userId, "REVIEW_COMPLETED",
                    "đã đánh giá " + request.getRating() + " sao cho cuốn sách \"" + book.getTitle() + "\"", bookId);
        } catch (Exception e) {
            // Ignore activity creation error
            e.printStackTrace();
        }

        return toDTO(review);
    }

    @Transactional
    public BookReviewDTO updateReview(Long userId, Long reviewId, CreateReviewRequest request) {
        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        review.setRating(request.getRating());
        review.setContent(request.getContent());
        bookReviewRepository.save(review);

        return toDTO(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        bookReviewRepository.delete(review);
    }

    private BookReviewDTO toDTO(BookReview review) {
        return BookReviewDTO.builder()
                .id(review.getId())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFullName())
                .userAvatarUrl(review.getUser().getAvatarUrl())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
