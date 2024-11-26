package com.example.hobiday_backend.domain.profile.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProfileResponse {
    private Long userId;
    private Long profileId;
    private String profileNickname;
    private String profileEmail;
    private List<String> profileGenre;
    private String profileIntroduction;
    private String profileImageUrl;
}