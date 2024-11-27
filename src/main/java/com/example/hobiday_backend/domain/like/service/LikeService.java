package com.example.hobiday_backend.domain.like.service;

import com.example.hobiday_backend.domain.feed.entity.Feed;
import com.example.hobiday_backend.domain.feed.exception.FeedErrorCode;
import com.example.hobiday_backend.domain.feed.exception.FeedException;
import com.example.hobiday_backend.domain.feed.repository.FeedRepository;
import com.example.hobiday_backend.domain.like.dto.LikeRes;
import com.example.hobiday_backend.domain.like.entity.Like;
import com.example.hobiday_backend.domain.like.repository.LikeRepository;
import com.example.hobiday_backend.domain.profile.entity.Profile;
import com.example.hobiday_backend.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final FeedRepository feedRepository;
    private final ProfileRepository profileRepository;

    public LikeRes toggleLike(Long feedId, Long userId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedErrorCode.FEED_NOT_FOUND));
        // userid로 프로필을 찾고 하면될려나
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다.")); // User ID로 Profile 찾기

        Optional<Like> existingLike = likeRepository.findByFeedAndProfile(feed, profile);
        boolean isLiked;

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            feed.decrementLikeCount();
            isLiked = false;
        } else {
            Like newLike = new Like(feed, profile);
            likeRepository.save(newLike);
            feed.incrementLikeCount();
            isLiked = true;
        }

        feedRepository.save(feed);

        return LikeRes.builder()
                .isLiked(isLiked)
                .likeCount(feed.getLikeCount())
                .build();
    }
}
