package org.example.dto.googlebooks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đơn giản hóa để trả về cho client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBookDTO {
    private String googleBookId;
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String description;
    private Integer pageCount;
    private String category;
    private String coverUrl;
    private String isbn;
    private String previewLink;
}
