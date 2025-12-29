package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.BookStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String coverUrl;
    private String isbn;
    private String description;
    private Integer totalPages;
    private Integer currentPage;
    private BookStatus status;
    private String location;
    private String category;
    private Integer rating;
    private LocalDate startedAt;
    private LocalDate finishedAt;
    private Integer progressPercentage;
}
