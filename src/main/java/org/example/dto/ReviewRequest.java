package org.example.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Request để submit review flashcard
 * quality: 0=Quên, 3=Nhớ, 5=Thuộc
 */
@Data
public class ReviewRequest {

    @Min(0)
    @Max(5)
    private Integer quality;
}
