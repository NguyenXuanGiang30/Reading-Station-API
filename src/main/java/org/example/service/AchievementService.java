package org.example.service;

import jakarta.annotation.PostConstruct;
import org.example.dto.AchievementDTO;
import org.example.model.Achievement;
import org.example.model.User;
import org.example.model.UserAchievement;
import org.example.repository.AchievementRepository;
import org.example.repository.UserAchievementRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository,
            UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.userRepository = userRepository;
    }

    // Khởi tạo các achievement mặc định nếu chưa có
    @PostConstruct
    public void initAchievements() {
        if (achievementRepository.count() == 0) {
            createAchievement("FIRST_BOOK", "Khởi đầu mới", "Hoàn thành cuốn sách đầu tiên", "📖", 1, "READING");
            createAchievement("BOOK_WORM", "Mọt sách", "Hoàn thành 10 cuốn sách", "🐛", 10, "READING");
            createAchievement("STREAK_7", "Kiên trì", "Đạt chuỗi đọc sách 7 ngày", "🔥", 7, "STREAK");
            createAchievement("REVIEWER", "Nhà phê bình", "Viết 5 đánh giá sách", "✍️", 5, "REVIEW");
            createAchievement("NOTE_TAKER", "Ghi chép chăm chỉ", "Tạo 20 ghi chú", "📝", 20, "NOTE");
        }
    }

    private void createAchievement(String code, String name, String desc, String icon, int target, String type) {
        Achievement a = Achievement.builder()
                .code(code)
                .name(name)
                .description(desc)
                .iconUrl(icon)
                .targetValue(target)
                .type(type)
                .build();
        achievementRepository.save(a);
    }

    public List<AchievementDTO> getUserAchievements(Long userId) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<UserAchievement> unlocked = userAchievementRepository.findByUserId(userId);

        Set<Long> unlockedIds = unlocked.stream()
                .map(ua -> ua.getAchievement().getId())
                .collect(Collectors.toSet());

        Map<Long, UserAchievement> userAchievementMap = unlocked.stream()
                .collect(Collectors.toMap(ua -> ua.getAchievement().getId(), ua -> ua));

        return allAchievements.stream().map(a -> {
            boolean isUnlocked = unlockedIds.contains(a.getId());
            return AchievementDTO.builder()
                    .id(a.getId())
                    .code(a.getCode())
                    .name(a.getName())
                    .description(a.getDescription())
                    .iconUrl(a.getIconUrl())
                    .targetValue(a.getTargetValue())
                    .type(a.getType())
                    .isUnlocked(isUnlocked)
                    .earnedAt(isUnlocked ? userAchievementMap.get(a.getId()).getEarnedAt() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void checkAndUnlock(Long userId, String type, int currentValue) {
        List<Achievement> connectAchievements = achievementRepository.findAll().stream()
                .filter(a -> a.getType().equals(type))
                .collect(Collectors.toList());

        for (Achievement a : connectAchievements) {
            if (currentValue >= a.getTargetValue()) {
                unlockAchievement(userId, a);
            }
        }
    }

    private void unlockAchievement(Long userId, Achievement achievement) {
        if (!userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement.getId())) {
            User user = userRepository.findById(userId).orElseThrow();
            UserAchievement ua = UserAchievement.builder()
                    .user(user)
                    .achievement(achievement)
                    .build();
            userAchievementRepository.save(ua);
        }
    }
}
