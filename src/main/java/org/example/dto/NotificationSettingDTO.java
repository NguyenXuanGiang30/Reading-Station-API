package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingDTO {
    private Long id;
    private Boolean enabled;
    private LocalTime reminderTime;
    private String reminderDays;
    private Boolean soundEnabled;
    private Boolean vibrationEnabled;
}
