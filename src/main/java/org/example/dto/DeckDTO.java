package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookCoverUrl;
    private String name;
    private String description;
    private String color;
    private Integer totalCards;
    private Integer dueCards;
    private Integer masteredPercentage;
}
