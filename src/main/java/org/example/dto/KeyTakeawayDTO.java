package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyTakeawayDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String content;
    private Integer pageNumber;
    private Integer orderIndex;
}
