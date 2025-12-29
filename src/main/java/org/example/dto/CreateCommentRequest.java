package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(max = 1000, message = "Nội dung bình luận tối đa 1000 ký tự")
    private String content;
}
