package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardDTO {
    private Long id;
    private Long deckId;
    private String question;
    private String answer;
    private Integer interval;
    private Integer repetition;
    private Double easeFactor;
    private LocalDate nextReviewDate;
    private Boolean isDue;
}
