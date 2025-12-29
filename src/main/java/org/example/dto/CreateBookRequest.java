package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.model.enums.BookStatus;

@Data
public class CreateBookRequest {

    @NotBlank(message = "Tên sách không được để trống")
    private String title;

    private String author;
    private String coverUrl;
    private String isbn;
    private String description;
    private Integer totalPages;
    private String location;
    private String category;
    private BookStatus status = BookStatus.WANT_TO_READ;
}
