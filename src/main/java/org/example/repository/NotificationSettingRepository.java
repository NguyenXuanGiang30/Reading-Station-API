package org.example.repository;

import org.example.model.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    Optional<NotificationSetting> findByUserId(Long userId);
}
