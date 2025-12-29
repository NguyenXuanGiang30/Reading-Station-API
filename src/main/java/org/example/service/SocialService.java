package org.example.service;

import org.example.dto.ActivityDTO;
import org.example.dto.CommentDTO;
import org.example.dto.CreateCommentRequest;
import org.example.dto.FriendDTO;
import org.example.model.Activity;
import org.example.model.ActivityComment;
import org.example.model.ActivityLike;
import org.example.model.Friendship;
import org.example.model.User;
import org.example.model.enums.FriendStatus;
import org.example.repository.ActivityCommentRepository;
import org.example.repository.ActivityLikeRepository;
import org.example.repository.ActivityRepository;
import org.example.repository.BookRepository;
import org.example.repository.FriendshipRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialService {

        private final FriendshipRepository friendshipRepository;
        private final ActivityRepository activityRepository;
        private final ActivityLikeRepository activityLikeRepository;
        private final ActivityCommentRepository activityCommentRepository;
        private final UserRepository userRepository;
        private final BookRepository bookRepository;

        public SocialService(FriendshipRepository friendshipRepository,
                        ActivityRepository activityRepository,
                        ActivityLikeRepository activityLikeRepository,
                        ActivityCommentRepository activityCommentRepository,
                        UserRepository userRepository,
                        BookRepository bookRepository) {
                this.friendshipRepository = friendshipRepository;
                this.activityRepository = activityRepository;
                this.activityLikeRepository = activityLikeRepository;
                this.activityCommentRepository = activityCommentRepository;
                this.userRepository = userRepository;
                this.bookRepository = bookRepository;
        }

        // ==================== FRIENDS ====================

        public List<FriendDTO> getFriends(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return friendshipRepository.findByUserAndStatus(user, FriendStatus.ACCEPTED).stream()
                                .map(f -> toFriendDTO(f, f.getFriend()))
                                .collect(Collectors.toList());
        }

        public List<FriendDTO> getPendingRequests(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return friendshipRepository.findByFriendAndStatus(user, FriendStatus.PENDING).stream()
                                .map(f -> toFriendDTO(f, f.getUser()))
                                .collect(Collectors.toList());
        }

        @Transactional
        public FriendDTO sendFriendRequest(Long friendId, Long userId) {
                if (friendId.equals(userId)) {
                        throw new RuntimeException("Cannot add yourself as friend");
                }

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                User friend = userRepository.findById(friendId)
                                .orElseThrow(() -> new RuntimeException("Friend not found"));

                // Check if already friends or pending
                if (friendshipRepository.findByUserAndFriend(user, friend).isPresent()) {
                        throw new RuntimeException("Friend request already exists");
                }

                Friendship friendship = Friendship.builder()
                                .user(user)
                                .friend(friend)
                                .status(FriendStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build();

                friendshipRepository.save(friendship);
                return toFriendDTO(friendship, friend);
        }

        @Transactional
        public FriendDTO acceptFriendRequest(Long friendshipId, Long userId) {
                Friendship friendship = friendshipRepository.findById(friendshipId)
                                .orElseThrow(() -> new RuntimeException("Friend request not found"));

                if (!friendship.getFriend().getId().equals(userId)) {
                        throw new RuntimeException("Access denied");
                }

                friendship.setStatus(FriendStatus.ACCEPTED);
                friendshipRepository.save(friendship);

                // Create reverse friendship
                Friendship reverseFriendship = Friendship.builder()
                                .user(friendship.getFriend())
                                .friend(friendship.getUser())
                                .status(FriendStatus.ACCEPTED)
                                .createdAt(LocalDateTime.now())
                                .build();
                friendshipRepository.save(reverseFriendship);

                return toFriendDTO(friendship, friendship.getUser());
        }

        @Transactional
        public void rejectFriendRequest(Long friendshipId, Long userId) {
                Friendship friendship = friendshipRepository.findById(friendshipId)
                                .orElseThrow(() -> new RuntimeException("Friend request not found"));

                if (!friendship.getFriend().getId().equals(userId)) {
                        throw new RuntimeException("Access denied");
                }

                friendship.setStatus(FriendStatus.REJECTED);
                friendshipRepository.save(friendship);
        }

        @Transactional
        public void removeFriend(Long friendId, Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                User friend = userRepository.findById(friendId)
                                .orElseThrow(() -> new RuntimeException("Friend not found"));

                friendshipRepository.findByUserAndFriend(user, friend)
                                .ifPresent(friendshipRepository::delete);
                friendshipRepository.findByUserAndFriend(friend, user)
                                .ifPresent(friendshipRepository::delete);
        }

        // ==================== ACTIVITY FEED ====================

        public List<ActivityDTO> getFriendActivities(Long userId, int limit) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                List<User> friends = friendshipRepository.findByUserAndStatus(user, FriendStatus.ACCEPTED)
                                .stream()
                                .map(Friendship::getFriend)
                                .collect(Collectors.toList());

                if (friends.isEmpty()) {
                        return List.of();
                }

                return activityRepository.findByUserInOrderByCreatedAtDesc(friends, PageRequest.of(0, limit))
                                .stream()
                                .map(this::toActivityDTO)
                                .collect(Collectors.toList());
        }

        public List<ActivityDTO> getUserActivities(Long userId, int limit) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return activityRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, limit))
                                .stream()
                                .map(this::toActivityDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public void createActivity(Long userId, String activityType, String description, Long bookId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Activity activity = Activity.builder()
                                .user(user)
                                .activityType(activityType)
                                .description(description)
                                .createdAt(LocalDateTime.now())
                                .build();

                if (bookId != null) {
                        bookRepository.findById(bookId).ifPresent(activity::setBook);
                }

                activityRepository.save(activity);
        }

        // ==================== FRIEND PROFILE ====================

        public FriendDTO getFriendProfile(Long friendId, Long userId) {
                User friend = userRepository.findById(friendId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                int booksRead = (int) bookRepository.countCompletedBooks(friend);

                return FriendDTO.builder()
                                .friendId(friend.getId())
                                .fullName(friend.getFullName())
                                .email(friend.getEmail())
                                .avatarUrl(friend.getAvatarUrl())
                                .bio(friend.getBio())
                                .booksRead(booksRead)
                                .streak(friend.getStreak())
                                .build();
        }

        // ==================== HELPERS ====================

        private FriendDTO toFriendDTO(Friendship friendship, User friendUser) {
                int booksRead = (int) bookRepository.countCompletedBooks(friendUser);

                return FriendDTO.builder()
                                .id(friendship.getId())
                                .friendId(friendUser.getId())
                                .fullName(friendUser.getFullName())
                                .email(friendUser.getEmail())
                                .avatarUrl(friendUser.getAvatarUrl())
                                .bio(friendUser.getBio())
                                .status(friendship.getStatus())
                                .booksRead(booksRead)
                                .streak(friendUser.getStreak())
                                .friendSince(friendship.getStatus() == FriendStatus.ACCEPTED ? friendship.getCreatedAt()
                                                : null)
                                .build();
        }

        private ActivityDTO toActivityDTO(Activity activity) {
                return toActivityDTO(activity, null);
        }

        private ActivityDTO toActivityDTO(Activity activity, Long currentUserId) {
                boolean isLiked = false;
                if (currentUserId != null) {
                        User user = userRepository.findById(currentUserId).orElse(null);
                        if (user != null) {
                                isLiked = activityLikeRepository.existsByActivityAndUser(activity, user);
                        }
                }

                return ActivityDTO.builder()
                                .id(activity.getId())
                                .userId(activity.getUser().getId())
                                .userFullName(activity.getUser().getFullName())
                                .userAvatarUrl(activity.getUser().getAvatarUrl())
                                .activityType(activity.getActivityType())
                                .description(activity.getDescription())
                                .bookId(activity.getBook() != null ? activity.getBook().getId() : null)
                                .bookTitle(activity.getBook() != null ? activity.getBook().getTitle() : null)
                                .bookCoverUrl(activity.getBook() != null ? activity.getBook().getCoverUrl() : null)
                                .createdAt(activity.getCreatedAt())
                                .likesCount(activity.getLikesCount() != null ? activity.getLikesCount() : 0)
                                .commentsCount(activity.getCommentsCount() != null ? activity.getCommentsCount() : 0)
                                .isLiked(isLiked)
                                .build();
        }

        // ==================== LIKES ====================

        @Transactional
        public ActivityDTO likeActivity(Long activityId, Long userId) {
                Activity activity = activityRepository.findById(activityId)
                                .orElseThrow(() -> new RuntimeException("Activity not found"));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Kiểm tra đã like chưa
                if (activityLikeRepository.existsByActivityAndUser(activity, user)) {
                        throw new RuntimeException("Bạn đã thích bài viết này rồi");
                }

                // Tạo like
                ActivityLike like = ActivityLike.builder()
                                .activity(activity)
                                .user(user)
                                .build();
                activityLikeRepository.save(like);

                // Cập nhật số like
                activity.setLikesCount((activity.getLikesCount() != null ? activity.getLikesCount() : 0) + 1);
                activityRepository.save(activity);

                return toActivityDTO(activity, userId);
        }

        @Transactional
        public ActivityDTO unlikeActivity(Long activityId, Long userId) {
                Activity activity = activityRepository.findById(activityId)
                                .orElseThrow(() -> new RuntimeException("Activity not found"));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Kiểm tra đã like chưa
                ActivityLike like = activityLikeRepository.findByActivityAndUser(activity, user)
                                .orElseThrow(() -> new RuntimeException("Bạn chưa thích bài viết này"));

                // Xóa like
                activityLikeRepository.delete(like);

                // Giảm số like
                int currentLikes = activity.getLikesCount() != null ? activity.getLikesCount() : 0;
                activity.setLikesCount(Math.max(0, currentLikes - 1));
                activityRepository.save(activity);

                return toActivityDTO(activity, userId);
        }

        @Transactional
        public ActivityDTO toggleLike(Long activityId, Long userId) {
                Activity activity = activityRepository.findById(activityId)
                                .orElseThrow(() -> new RuntimeException("Activity not found"));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (activityLikeRepository.existsByActivityAndUser(activity, user)) {
                        return unlikeActivity(activityId, userId);
                } else {
                        return likeActivity(activityId, userId);
                }
        }

        // ==================== COMMENTS ====================

        public List<CommentDTO> getActivityComments(Long activityId, Long userId) {
                Activity activity = activityRepository.findById(activityId)
                                .orElseThrow(() -> new RuntimeException("Activity not found"));

                return activityCommentRepository.findByActivityOrderByCreatedAtAsc(activity)
                                .stream()
                                .map(comment -> toCommentDTO(comment, userId))
                                .collect(Collectors.toList());
        }

        @Transactional
        public CommentDTO createComment(Long activityId, CreateCommentRequest request, Long userId) {
                Activity activity = activityRepository.findById(activityId)
                                .orElseThrow(() -> new RuntimeException("Activity not found"));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                ActivityComment comment = ActivityComment.builder()
                                .activity(activity)
                                .user(user)
                                .content(request.getContent())
                                .build();
                activityCommentRepository.save(comment);

                // Cập nhật số comment
                activity.setCommentsCount((activity.getCommentsCount() != null ? activity.getCommentsCount() : 0) + 1);
                activityRepository.save(activity);

                return toCommentDTO(comment, userId);
        }

        @Transactional
        public CommentDTO updateComment(Long commentId, CreateCommentRequest request, Long userId) {
                ActivityComment comment = activityCommentRepository.findById(commentId)
                                .orElseThrow(() -> new RuntimeException("Comment not found"));

                if (!comment.getUser().getId().equals(userId)) {
                        throw new RuntimeException("Bạn không có quyền chỉnh sửa bình luận này");
                }

                comment.setContent(request.getContent());
                activityCommentRepository.save(comment);

                return toCommentDTO(comment, userId);
        }

        @Transactional
        public void deleteComment(Long commentId, Long userId) {
                ActivityComment comment = activityCommentRepository.findById(commentId)
                                .orElseThrow(() -> new RuntimeException("Comment not found"));

                if (!comment.getUser().getId().equals(userId)) {
                        throw new RuntimeException("Bạn không có quyền xóa bình luận này");
                }

                Activity activity = comment.getActivity();

                activityCommentRepository.delete(comment);

                // Giảm số comment
                int currentComments = activity.getCommentsCount() != null ? activity.getCommentsCount() : 0;
                activity.setCommentsCount(Math.max(0, currentComments - 1));
                activityRepository.save(activity);
        }

        private CommentDTO toCommentDTO(ActivityComment comment, Long currentUserId) {
                return CommentDTO.builder()
                                .id(comment.getId())
                                .activityId(comment.getActivity().getId())
                                .userId(comment.getUser().getId())
                                .userFullName(comment.getUser().getFullName())
                                .userAvatarUrl(comment.getUser().getAvatarUrl())
                                .content(comment.getContent())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .isOwner(comment.getUser().getId().equals(currentUserId))
                                .build();
        }
}
