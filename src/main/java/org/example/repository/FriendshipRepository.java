package org.example.repository;

import org.example.model.Friendship;
import org.example.model.User;
import org.example.model.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByUserAndStatus(User user, FriendStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.user = :user OR f.friend = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("user") User user);

    List<Friendship> findByFriendAndStatus(User friend, FriendStatus status);

    Optional<Friendship> findByUserAndFriend(User user, User friend);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f " +
            "WHERE ((f.user = :user1 AND f.friend = :user2) OR (f.user = :user2 AND f.friend = :user1)) " +
            "AND f.status = 'ACCEPTED'")
    boolean areFriends(@Param("user1") User user1, @Param("user2") User user2);
}
