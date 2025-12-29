package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.enums.FriendStatus;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho quan hệ bạn bè
 */
@Entity
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "friend_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status = FriendStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
