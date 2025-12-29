package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateNoteRequest {

    private Long bookId;
    private Integer pageNumber;

    @NotBlank(message = "Nội dung ghi chú không được để trống")
    private String content;

    private String tags;
    private Boolean isFromOcr = false;
}
