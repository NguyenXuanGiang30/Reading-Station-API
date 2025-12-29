package org.example.service;

import org.example.dto.NotificationSettingDTO;
import org.example.dto.UpdateNotificationSettingsRequest;
import org.example.model.NotificationSetting;
import org.example.model.User;
import org.example.repository.NotificationSettingRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class NotificationService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationSettingRepository notificationSettingRepository,
            UserRepository userRepository) {
        this.notificationSettingRepository = notificationSettingRepository;
        this.userRepository = userRepository;
    }

    public NotificationSettingDTO getSettings(Long userId) {
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
        return toDTO(setting);
    }

    @Transactional
    public NotificationSettingDTO updateSettings(Long userId, UpdateNotificationSettingsRequest request) {
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));

        if (request.getEnabled() != null)
            setting.setEnabled(request.getEnabled());
        if (request.getReminderTime() != null)
            setting.setReminderTime(request.getReminderTime());
        if (request.getReminderDays() != null)
            setting.setReminderDays(request.getReminderDays());
        if (request.getSoundEnabled() != null)
            setting.setSoundEnabled(request.getSoundEnabled());
        if (request.getVibrationEnabled() != null)
            setting.setVibrationEnabled(request.getVibrationEnabled());

        notificationSettingRepository.save(setting);
        return toDTO(setting);
    }

    private NotificationSetting createDefaultSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationSetting setting = NotificationSetting.builder()
                .user(user)
                .enabled(true)
                .reminderTime(LocalTime.of(20, 0))
                .reminderDays("MON,TUE,WED,THU,FRI,SAT,SUN")
                .soundEnabled(true)
                .vibrationEnabled(true)
                .build();

        return notificationSettingRepository.save(setting);
    }

    private NotificationSettingDTO toDTO(NotificationSetting setting) {
        return NotificationSettingDTO.builder()
                .id(setting.getId())
                .enabled(setting.getEnabled())
                .reminderTime(setting.getReminderTime())
                .reminderDays(setting.getReminderDays())
                .soundEnabled(setting.getSoundEnabled())
                .vibrationEnabled(setting.getVibrationEnabled())
                .build();
    }
}
