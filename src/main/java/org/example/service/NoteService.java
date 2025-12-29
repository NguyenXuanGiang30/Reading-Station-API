package org.example.service;

import org.example.dto.CreateNoteRequest;
import org.example.dto.NoteDTO;
import org.example.model.Book;
import org.example.model.Note;
import org.example.model.User;
import org.example.repository.BookRepository;
import org.example.repository.NoteRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<NoteDTO> getUserNotes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return noteRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NoteDTO> getBookNotes(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return noteRepository.findByBookOrderByPageNumberAsc(book)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public NoteDTO getNoteById(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return toDTO(note);
    }

    @Transactional
    public NoteDTO createNote(CreateNoteRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = null;
        if (request.getBookId() != null) {
            book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
        }

        Note note = Note.builder()
                .user(user)
                .book(book)
                .pageNumber(request.getPageNumber())
                .content(request.getContent())
                .tags(request.getTags())
                .isFromOcr(request.getIsFromOcr())
                .build();

        noteRepository.save(note);
        return toDTO(note);
    }

    @Transactional
    public NoteDTO updateNote(Long noteId, CreateNoteRequest request, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        note.setContent(request.getContent());
        note.setPageNumber(request.getPageNumber());
        note.setTags(request.getTags());

        if (request.getBookId() != null) {
            Book book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            note.setBook(book);
        }

        noteRepository.save(note);
        return toDTO(note);
    }

    @Transactional
    public void deleteNote(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        noteRepository.delete(note);
    }

    private NoteDTO toDTO(Note note) {
        NoteDTO dto = NoteDTO.builder()
                .id(note.getId())
                .pageNumber(note.getPageNumber())
                .content(note.getContent())
                .tags(note.getTags())
                .isFromOcr(note.getIsFromOcr())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();

        if (note.getBook() != null) {
            dto.setBookId(note.getBook().getId());
            dto.setBookTitle(note.getBook().getTitle());
        }

        return dto;
    }
}
