package org.example.dto.googlebooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Response từ Google Books API
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookResponse {
    private int totalItems;
    private List<GoogleBookItem> items;
}
