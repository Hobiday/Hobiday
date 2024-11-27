package com.example.hobiday_backend.domain.comment.service;

import com.example.hobiday_backend.domain.comment.dto.CommentReq;
import com.example.hobiday_backend.domain.comment.dto.CommentRes;
import com.example.hobiday_backend.domain.comment.entity.Comment;
import com.example.hobiday_backend.domain.comment.exception.CommentErrorCode;
import com.example.hobiday_backend.domain.comment.exception.CommentException;
import com.example.hobiday_backend.domain.member.entity.Member;
import com.example.hobiday_backend.domain.comment.repository.CommentRepository;
import com.example.hobiday_backend.domain.feed.entity.Feed;
import com.example.hobiday_backend.domain.feed.repository.FeedRepository;
import com.example.hobiday_backend.domain.profile.entity.Profile;
import com.example.hobiday_backend.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;
    private final ProfileRepository profileRepository;

    public CommentRes createComment(Long feedId, CommentReq commentReq, Member member) {
        // 피드 프로필 예외 추가 시켜줘야 함
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new RuntimeException("피드를 찾을 수 없습니다."));
        Profile profile = profileRepository.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));

        Comment comment = new Comment(commentReq.getContents(), feed, profile); //
        Comment savedComment = commentRepository.save(comment);

        return CommentRes.from(savedComment);
    }

    public List<CommentRes> getCommentsByFeedId(Long feedId) {
        List<Comment> comments = commentRepository.findAllByFeedIdOrderByCreatedTimeAsc(feedId);
        return comments.stream()
                .map(CommentRes::from)
                .collect(Collectors.toList());
    }

    public CommentRes updateComment(Long commentId, CommentReq commentReq, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getProfile().getMember().getId().equals(member.getId())) {
            throw new CommentException(CommentErrorCode.COMMENT_UPDATE_ACCESS_DENIED);
        }

        comment.updateContents(commentReq.getContents());
        return CommentRes.from(commentRepository.save(comment));
    }

    public void deleteComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getProfile().getMember().getId().equals(member.getId())) {
            throw new CommentException(CommentErrorCode.COMMENT_DELETE_ACCESS_DENIED);
        }

        commentRepository.delete(comment);
    }
}
