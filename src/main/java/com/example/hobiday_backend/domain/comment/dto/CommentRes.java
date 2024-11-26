package com.example.hobiday_backend.domain.comment.dto;

import com.example.hobiday_backend.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentRes {
    private Long id;
    private String contents;
    private String profileName;
    private String profileImageUrl;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private boolean isAuthor;

    public static CommentRes from(Comment comment) {
        return CommentRes.builder()
                .id(comment.getId())
                .contents(comment.getContents())
                .profileName(comment.getProfile().getProfileNickname())
                .profileImageUrl(comment.getProfile().getProfileImageUrl())
                .createdTime(comment.getCreatedTime())
                .modifiedTime(comment.getModifiedTime())
                .build();
    }
}

