package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho người dùng
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer streak = 0;

    @Column(name = "last_activity_date")
    private java.time.LocalDate lastActivityDate;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Deck> decks = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NotificationSetting notificationSetting;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
