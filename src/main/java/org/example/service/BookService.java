package org.example.service;

import org.example.dto.BookDTO;
import org.example.dto.CreateBookRequest;
import org.example.dto.googlebooks.GoogleBookDTO;
import org.example.model.Book;
import org.example.model.User;
import org.example.model.enums.BookStatus;
import org.example.repository.BookRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final GoogleBooksService googleBooksService;

    public BookService(BookRepository bookRepository, UserRepository userRepository,
            GoogleBooksService googleBooksService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.googleBooksService = googleBooksService;
    }

    /**
     * Thêm sách vào thư viện bằng mã vạch (ISBN)
     * Tự động lấy thông tin từ Google Books API
     */
    @Transactional
    public BookDTO createBookFromIsbn(String isbn, BookStatus status, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm thông tin sách từ Google Books
        GoogleBookDTO googleBook = googleBooksService.searchByIsbn(isbn);
        if (googleBook == null) {
            throw new RuntimeException("Không tìm thấy sách với ISBN: " + isbn);
        }

        // Tạo sách mới từ thông tin Google Books
        Book book = Book.builder()
                .user(user)
                .title(googleBook.getTitle())
                .author(googleBook.getAuthor())
                .coverUrl(googleBook.getCoverUrl())
                .isbn(isbn)
                .description(googleBook.getDescription())
                .totalPages(googleBook.getPageCount())
                .currentPage(0)
                .status(status != null ? status : BookStatus.WANT_TO_READ)
                .category(googleBook.getCategory())
                .rating(0)
                .build();

        if (book.getStatus() == BookStatus.READING) {
            book.setStartedAt(LocalDate.now());
        }

        bookRepository.save(book);
        return toDTO(book);
    }

    public List<BookDTO> getUserBooks(Long userId, BookStatus status, String search) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Book> books;
        if (search != null && !search.isEmpty()) {
            books = bookRepository.searchBooks(user, search);
        } else if (status != null) {
            books = bookRepository.findByUserAndStatusOrderByUpdatedAtDesc(user, status);
        } else {
            books = bookRepository.findByUserOrderByUpdatedAtDesc(user);
        }

        return books.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BookDTO getBookById(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return toDTO(book);
    }

    @Transactional
    public BookDTO createBook(CreateBookRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = Book.builder()
                .user(user)
                .title(request.getTitle())
                .author(request.getAuthor())
                .coverUrl(request.getCoverUrl())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .totalPages(request.getTotalPages())
                .currentPage(0)
                .status(request.getStatus() != null ? request.getStatus() : BookStatus.WANT_TO_READ)
                .location(request.getLocation())
                .category(request.getCategory())
                .rating(0)
                .build();

        if (book.getStatus() == BookStatus.READING) {
            book.setStartedAt(LocalDate.now());
        }

        bookRepository.save(book);
        return toDTO(book);
    }

    @Transactional
    public BookDTO updateBook(Long bookId, CreateBookRequest request, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCoverUrl(request.getCoverUrl());
        book.setDescription(request.getDescription());
        book.setTotalPages(request.getTotalPages());
        book.setLocation(request.getLocation());
        book.setCategory(request.getCategory());

        if (request.getStatus() != null) {
            BookStatus oldStatus = book.getStatus();
            book.setStatus(request.getStatus());

            if (oldStatus != BookStatus.READING && request.getStatus() == BookStatus.READING) {
                book.setStartedAt(LocalDate.now());
            }
            if (request.getStatus() == BookStatus.COMPLETED && book.getFinishedAt() == null) {
                book.setFinishedAt(LocalDate.now());
            }
        }

        bookRepository.save(book);
        return toDTO(book);
    }

    @Transactional
    public BookDTO updateProgress(Long bookId, Integer currentPage, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        book.setCurrentPage(currentPage);

        if (book.getStatus() == BookStatus.WANT_TO_READ) {
            book.setStatus(BookStatus.READING);
            book.setStartedAt(LocalDate.now());
        }

        if (book.getTotalPages() != null && currentPage >= book.getTotalPages()) {
            book.setStatus(BookStatus.COMPLETED);
            book.setFinishedAt(LocalDate.now());
        }

        bookRepository.save(book);
        return toDTO(book);
    }

    @Transactional
    public void deleteBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        bookRepository.delete(book);
    }

    private BookDTO toDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverUrl(book.getCoverUrl())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .totalPages(book.getTotalPages())
                .currentPage(book.getCurrentPage())
                .status(book.getStatus())
                .location(book.getLocation())
                .category(book.getCategory())
                .rating(book.getRating())
                .startedAt(book.getStartedAt())
                .finishedAt(book.getFinishedAt())
                .progressPercentage(book.getProgressPercentage())
                .build();
    }
}
