package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationSettingsRequest {
    private Boolean enabled;
    private LocalTime reminderTime;
    private String reminderDays; // "MON,TUE,WED"
    private Boolean soundEnabled;
    private Boolean vibrationEnabled;
}
