package com.example.hobiday_backend.domain.comment.repository;

import com.example.hobiday_backend.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFeedIdAndParentCommentIsNull(Long feedId);
}
