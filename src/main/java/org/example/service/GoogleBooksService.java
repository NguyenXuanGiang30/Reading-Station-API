package org.example.service;

import org.example.dto.googlebooks.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service để gọi Google Books API
 */
@Service
public class GoogleBooksService {

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";

    @Value("${google.books.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GoogleBooksService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Tìm kiếm sách theo từ khóa
     */
    public List<GoogleBookDTO> searchBooks(String query, int maxResults) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE_BOOKS_API_URL)
                .queryParam("q", query)
                .queryParam("maxResults", Math.min(maxResults, 40))
                .queryParam("printType", "books")
                .queryParam("langRestrict", "vi,en");

        if (apiKey != null && !apiKey.isEmpty()) {
            builder.queryParam("key", apiKey);
        }

        try {
            GoogleBookResponse response = restTemplate.getForObject(
                    builder.toUriString(),
                    GoogleBookResponse.class);

            if (response == null || response.getItems() == null) {
                return new ArrayList<>();
            }

            return response.getItems().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi Google Books API: " + e.getMessage());
        }
    }

    /**
     * Tìm kiếm sách theo ISBN
     */
    public GoogleBookDTO searchByIsbn(String isbn) {
        String query = "isbn:" + isbn;
        List<GoogleBookDTO> results = searchBooks(query, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Lấy chi tiết sách theo Google Book ID
     */
    public GoogleBookDTO getBookById(String googleBookId) {
        String url = GOOGLE_BOOKS_API_URL + "/" + googleBookId;

        if (apiKey != null && !apiKey.isEmpty()) {
            url += "?key=" + apiKey;
        }

        try {
            GoogleBookItem item = restTemplate.getForObject(url, GoogleBookItem.class);
            return item != null ? toDTO(item) : null;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin sách: " + e.getMessage());
        }
    }

    /**
     * Convert GoogleBookItem thành GoogleBookDTO
     */
    private GoogleBookDTO toDTO(GoogleBookItem item) {
        VolumeInfo info = item.getVolumeInfo();
        if (info == null) {
            return GoogleBookDTO.builder()
                    .googleBookId(item.getId())
                    .build();
        }

        // Lấy ISBN (ưu tiên ISBN-13)
        String isbn = null;
        if (info.getIndustryIdentifiers() != null) {
            isbn = info.getIndustryIdentifiers().stream()
                    .filter(id -> "ISBN_13".equals(id.getType()))
                    .map(VolumeInfo.IndustryIdentifier::getIdentifier)
                    .findFirst()
                    .orElse(info.getIndustryIdentifiers().stream()
                            .filter(id -> "ISBN_10".equals(id.getType()))
                            .map(VolumeInfo.IndustryIdentifier::getIdentifier)
                            .findFirst()
                            .orElse(null));
        }

        // Lấy URL ảnh bìa (thay http -> https)
        String coverUrl = null;
        if (info.getImageLinks() != null && info.getImageLinks().getThumbnail() != null) {
            coverUrl = info.getImageLinks().getThumbnail().replace("http://", "https://");
        }

        return GoogleBookDTO.builder()
                .googleBookId(item.getId())
                .title(info.getTitle())
                .author(info.getAuthors() != null ? String.join(", ", info.getAuthors()) : null)
                .publisher(info.getPublisher())
                .publishedDate(info.getPublishedDate())
                .description(info.getDescription())
                .pageCount(info.getPageCount())
                .category(info.getCategories() != null && !info.getCategories().isEmpty()
                        ? info.getCategories().get(0)
                        : null)
                .coverUrl(coverUrl)
                .isbn(isbn)
                .previewLink(info.getPreviewLink())
                .build();
    }
}
