package org.example.dto.googlebooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Thông tin chi tiết của sách từ Google Books API
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeInfo {
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private Integer pageCount;
    private List<String> categories;
    private ImageLinks imageLinks;
    private String language;
    private String previewLink;
    private String infoLink;

    // ISBN identifiers
    private List<IndustryIdentifier> industryIdentifiers;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageLinks {
        private String smallThumbnail;
        private String thumbnail;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IndustryIdentifier {
        private String type;
        private String identifier;
    }
}
