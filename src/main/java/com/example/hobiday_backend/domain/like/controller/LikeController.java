package com.example.hobiday_backend.domain.like.controller;

import com.example.hobiday_backend.domain.like.dto.LikeRes;
import com.example.hobiday_backend.domain.like.service.LikeService;
import com.example.hobiday_backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "좋아요 API") // 컨트롤러 태그
public class LikeController {

    private final LikeService likeService;
    private final MemberService memberService;

    @Operation(
            summary = "좋아요 추가/취소",
            description = "피드 ID를 기반으로 좋아요를 추가하거나 취소합니다.",
            parameters = {
                    @Parameter(name = "feedId", description = "좋아요를 추가/취소할 피드의 ID", required = true),
                    @Parameter(name = "token", description = "사용자 인증 토큰 (헤더)", required = true)
            }
    )
    @PostMapping("/{feedId}")
    public ResponseEntity<LikeRes> toggleLike(
            @PathVariable Long feedId,
            @RequestHeader String token) {
        Long userId = memberService.getMemberIdByToken(token);
        LikeRes likeRes = likeService.toggleLike(feedId, userId);
        return ResponseEntity.ok(likeRes);
    }
}

