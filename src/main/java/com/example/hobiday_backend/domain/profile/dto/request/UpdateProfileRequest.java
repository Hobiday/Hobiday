package com.example.hobiday_backend.domain.profile.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateProfileRequest {
    public String profileNickname;
    public List<String> profileGenre;
    public String profileIntroduction;
    public String profileImageUrl;
}
