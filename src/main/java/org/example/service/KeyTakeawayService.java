package org.example.service;

import org.example.dto.KeyTakeawayDTO;
import org.example.model.Book;
import org.example.model.KeyTakeaway;
import org.example.repository.BookRepository;
import org.example.repository.KeyTakeawayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeyTakeawayService {

    private final KeyTakeawayRepository keyTakeawayRepository;
    private final BookRepository bookRepository;

    public KeyTakeawayService(KeyTakeawayRepository keyTakeawayRepository, BookRepository bookRepository) {
        this.keyTakeawayRepository = keyTakeawayRepository;
        this.bookRepository = bookRepository;
    }

    public List<KeyTakeawayDTO> getBookTakeaways(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return keyTakeawayRepository.findByBookOrderByOrderIndexAsc(book).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public KeyTakeawayDTO createTakeaway(Long bookId, String content, Integer pageNumber, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        int maxOrder = keyTakeawayRepository.findMaxOrderIndexByBook(book).orElse(0);

        KeyTakeaway takeaway = KeyTakeaway.builder()
                .book(book)
                .content(content)
                .pageNumber(pageNumber)
                .orderIndex(maxOrder + 1)
                .build();

        keyTakeawayRepository.save(takeaway);
        return toDTO(takeaway);
    }

    @Transactional
    public KeyTakeawayDTO updateTakeaway(Long id, String content, Integer pageNumber, Long userId) {
        KeyTakeaway takeaway = keyTakeawayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Takeaway not found"));

        if (!takeaway.getBook().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        takeaway.setContent(content);
        takeaway.setPageNumber(pageNumber);
        keyTakeawayRepository.save(takeaway);
        return toDTO(takeaway);
    }

    @Transactional
    public void deleteTakeaway(Long id, Long userId) {
        KeyTakeaway takeaway = keyTakeawayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Takeaway not found"));

        if (!takeaway.getBook().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        keyTakeawayRepository.delete(takeaway);
    }

    @Transactional
    public void reorderTakeaways(Long bookId, List<Long> orderedIds, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        for (int i = 0; i < orderedIds.size(); i++) {
            KeyTakeaway takeaway = keyTakeawayRepository.findById(orderedIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Takeaway not found"));
            takeaway.setOrderIndex(i + 1);
            keyTakeawayRepository.save(takeaway);
        }
    }

    private KeyTakeawayDTO toDTO(KeyTakeaway takeaway) {
        return KeyTakeawayDTO.builder()
                .id(takeaway.getId())
                .bookId(takeaway.getBook().getId())
                .bookTitle(takeaway.getBook().getTitle())
                .content(takeaway.getContent())
                .pageNumber(takeaway.getPageNumber())
                .orderIndex(takeaway.getOrderIndex())
                .build();
    }
}
