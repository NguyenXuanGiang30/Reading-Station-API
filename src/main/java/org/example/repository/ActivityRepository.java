package org.example.repository;

import org.example.model.Activity;
import org.example.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Activity> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.user IN :users ORDER BY a.createdAt DESC")
    List<Activity> findByUserInOrderByCreatedAtDesc(@Param("users") List<User> users, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.user.id IN :friendIds ORDER BY a.createdAt DESC")
    List<Activity> findFriendActivities(@Param("friendIds") List<Long> friendIds);
}
