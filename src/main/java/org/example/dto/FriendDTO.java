package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.FriendStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    private Long id;
    private Long friendId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String bio;
    private FriendStatus status;
    private Integer booksRead;
    private Integer streak;
    private LocalDateTime friendSince;
}
