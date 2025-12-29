package org.example.service;

import org.example.dto.DeckDTO;
import org.example.dto.FlashcardDTO;
import org.example.dto.ReviewRequest;
import org.example.model.*;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public FlashcardService(FlashcardRepository flashcardRepository, DeckRepository deckRepository,
            NoteRepository noteRepository, UserRepository userRepository) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<DeckDTO> getUserDecks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return deckRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::toDeckDTO).collect(Collectors.toList());
    }

    public DeckDTO getDeckById(Long deckId, Long userId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        if (!deck.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return toDeckDTO(deck);
    }

    @Transactional
    public DeckDTO createDeck(String name, String description, Long bookId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Deck deck = Deck.builder()
                .user(user)
                .name(name)
                .description(description)
                .color("#1A4D3E")
                .build();

        deckRepository.save(deck);
        return toDeckDTO(deck);
    }

    public List<FlashcardDTO> getDeckCards(Long deckId, Long userId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        if (!deck.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return flashcardRepository.findByDeck(deck)
                .stream().map(this::toFlashcardDTO).collect(Collectors.toList());
    }

    public List<FlashcardDTO> getDueCards(Long userId) {
        return flashcardRepository.findDueCards(userId, LocalDate.now())
                .stream().map(this::toFlashcardDTO).collect(Collectors.toList());
    }

    @Transactional
    public FlashcardDTO createFlashcard(Long deckId, String question, String answer, Long userId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        if (!deck.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        Flashcard flashcard = Flashcard.builder()
                .deck(deck)
                .question(question)
                .answer(answer)
                .interval(1)
                .repetition(0)
                .easeFactor(2.5)
                .nextReviewDate(LocalDate.now())
                .build();

        flashcardRepository.save(flashcard);
        return toFlashcardDTO(flashcard);
    }

    @Transactional
    public FlashcardDTO createFromNote(Long noteId, Long deckId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        if (!deck.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // Parse note content - simple logic: first line as question, rest as answer
        String content = note.getContent();
        String question = content.length() > 100 ? content.substring(0, 100) + "?" : content + "?";
        String answer = content;

        Flashcard flashcard = Flashcard.builder()
                .deck(deck)
                .note(note)
                .question(question)
                .answer(answer)
                .interval(1)
                .repetition(0)
                .easeFactor(2.5)
                .nextReviewDate(LocalDate.now())
                .build();

        flashcardRepository.save(flashcard);
        return toFlashcardDTO(flashcard);
    }

    @Transactional
    public FlashcardDTO reviewFlashcard(Long flashcardId, ReviewRequest request, Long userId) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));

        if (!flashcard.getDeck().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // Process SM-2 algorithm
        flashcard.processReview(request.getQuality());
        flashcardRepository.save(flashcard);

        return toFlashcardDTO(flashcard);
    }

    @Transactional
    public void deleteFlashcard(Long flashcardId, Long userId) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));

        if (!flashcard.getDeck().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        flashcardRepository.delete(flashcard);
    }

    private DeckDTO toDeckDTO(Deck deck) {
        DeckDTO dto = DeckDTO.builder()
                .id(deck.getId())
                .name(deck.getName())
                .description(deck.getDescription())
                .color(deck.getColor())
                .totalCards(deck.getTotalCards())
                .dueCards((int) deck.getDueCardsCount())
                .masteredPercentage(deck.getMasteredPercentage())
                .build();

        if (deck.getBook() != null) {
            dto.setBookId(deck.getBook().getId());
            dto.setBookTitle(deck.getBook().getTitle());
            dto.setBookCoverUrl(deck.getBook().getCoverUrl());
        }

        return dto;
    }

    private FlashcardDTO toFlashcardDTO(Flashcard flashcard) {
        return FlashcardDTO.builder()
                .id(flashcard.getId())
                .deckId(flashcard.getDeck().getId())
                .question(flashcard.getQuestion())
                .answer(flashcard.getAnswer())
                .interval(flashcard.getInterval())
                .repetition(flashcard.getRepetition())
                .easeFactor(flashcard.getEaseFactor())
                .nextReviewDate(flashcard.getNextReviewDate())
                .isDue(flashcard.isDue())
                .build();
    }
}
