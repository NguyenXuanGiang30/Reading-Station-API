package org.example.repository;

import org.example.model.Activity;
import org.example.model.ActivityLike;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityLikeRepository extends JpaRepository<ActivityLike, Long> {

    Optional<ActivityLike> findByActivityAndUser(Activity activity, User user);

    boolean existsByActivityAndUser(Activity activity, User user);

    long countByActivity(Activity activity);

    void deleteByActivityAndUser(Activity activity, User user);
}
