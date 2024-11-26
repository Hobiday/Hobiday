package com.example.hobiday_backend.domain.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddProfileRequest {
    public String profileNickname;
    public List<String> profileGenre;
    // 이메일은 수정 못하도록 필드 선언 안함
}