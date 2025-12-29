package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private Integer streak;
    private Long booksRead;
    private Long totalNotes;
    private Long totalFlashcards;
}
