package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Integer pageNumber;
    private String content;
    private String tags;
    private Boolean isFromOcr;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
