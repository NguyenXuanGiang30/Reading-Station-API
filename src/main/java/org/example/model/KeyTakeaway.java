package org.example.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity đại diện cho key takeaway của sách
 */
@Entity
@Table(name = "key_takeaways")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyTakeaway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "order_index", columnDefinition = "INT DEFAULT 0")
    private Integer orderIndex = 0;
}
