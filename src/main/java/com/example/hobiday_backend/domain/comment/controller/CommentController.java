package com.example.hobiday_backend.domain.comment.controller;

import com.example.hobiday_backend.domain.comment.dto.CommentReq;
import com.example.hobiday_backend.domain.comment.dto.CommentRes;
import com.example.hobiday_backend.domain.comment.service.CommentService;
import com.example.hobiday_backend.domain.member.entity.Member;
import com.example.hobiday_backend.domain.member.service.MemberService;
import com.example.hobiday_backend.global.dto.SuccessRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@Tag(name = "Comment", description = "댓글 API") // 컨트롤러 전체 설명
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;

    @Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.") // 메서드 설명
    @PostMapping("/{feedId}")
    public ResponseEntity<SuccessRes<CommentRes>> createComment(
            @PathVariable Long feedId,
            @RequestBody CommentReq commentReq,
            @RequestHeader("Authorization") String token) {
        Long userId = memberService.getMemberIdByToken(token);
        Member member = memberService.findById(userId);
        CommentRes comment = commentService.createComment(feedId, commentReq, member);
        return ResponseEntity.ok(SuccessRes.success(comment));
    }

    @Operation(summary = "댓글 조회", description = "피드 ID를 기반으로 댓글 목록을 조회합니다.")
    @GetMapping("/{feedId}")
    public ResponseEntity<SuccessRes<List<CommentRes>>> getCommentsByFeedId(@PathVariable Long feedId) {
        List<CommentRes> comments = commentService.getCommentsByFeedId(feedId);
        return ResponseEntity.ok(SuccessRes.success(comments));
    }

    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @PutMapping("/{commentId}")
    public ResponseEntity<SuccessRes<CommentRes>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentReq commentReq,
            @RequestHeader("Authorization") String token) {
        Long userId = memberService.getMemberIdByToken(token);
        Member member = memberService.findById(userId);
        CommentRes updatedComment = commentService.updateComment(commentId, commentReq, member);
        return ResponseEntity.ok(SuccessRes.success(updatedComment));
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<SuccessRes<Void>> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String token) {
        Long userId = memberService.getMemberIdByToken(token);
        Member member = memberService.findById(userId);
        commentService.deleteComment(commentId, member);
        return ResponseEntity.ok(SuccessRes.success(null));
    }
}
