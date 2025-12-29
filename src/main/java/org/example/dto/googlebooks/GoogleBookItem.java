package org.example.dto.googlebooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Một item sách từ Google Books API
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookItem {
    private String id;
    private VolumeInfo volumeInfo;
}
