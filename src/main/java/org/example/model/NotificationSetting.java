package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Entity cài đặt thông báo của user
 */
@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled = true;

    @Column(name = "reminder_time")
    private LocalTime reminderTime;

    @Column(name = "reminder_days", length = 50)
    private String reminderDays = "MON,TUE,WED,THU,FRI";

    @Column(name = "sound_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean soundEnabled = true;

    @Column(name = "vibration_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean vibrationEnabled = true;

    @PrePersist
    protected void onCreate() {
        if (reminderTime == null) {
            reminderTime = LocalTime.of(20, 0); // 20:00 default
        }
    }
}
