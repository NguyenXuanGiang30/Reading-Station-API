package org.example.repository;

import org.example.model.Activity;
import org.example.model.ActivityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityCommentRepository extends JpaRepository<ActivityComment, Long> {

    List<ActivityComment> findByActivityOrderByCreatedAtAsc(Activity activity);

    List<ActivityComment> findByActivityOrderByCreatedAtDesc(Activity activity);

    long countByActivity(Activity activity);
}
