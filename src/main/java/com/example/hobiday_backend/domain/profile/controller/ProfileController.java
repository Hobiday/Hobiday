package com.example.hobiday_backend.domain.profile.controller;

import com.example.hobiday_backend.domain.profile.dto.request.AddProfileRequest;
import com.example.hobiday_backend.domain.profile.dto.request.UpdateProfileRequest;
import com.example.hobiday_backend.domain.profile.dto.response.ProfileMessageResponse;
import com.example.hobiday_backend.domain.profile.dto.response.ProfileRegistrationResponse;
import com.example.hobiday_backend.domain.profile.dto.response.ProfileResponse;
import com.example.hobiday_backend.domain.profile.entity.Profile;
import com.example.hobiday_backend.domain.profile.repository.ProfileRepository;
import com.example.hobiday_backend.domain.profile.service.ProfileService;
import com.example.hobiday_backend.domain.member.repository.MemberRepository;
import com.example.hobiday_backend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Profiles", description = "프로필 API")
public class ProfileController {
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 프로필 등록(처음)하는 api
    @Operation(summary = "프로필 등록(온보딩 작성) API", description = "토큰과 온보딩 작성한 데이터(닉네임, 장르)를 요청 받아 프로필 등록하고 반환합니다" +
            "\n{\"연극\", \"무용\", \"대중무용\", \"서양음악\", \"한국음악\", \"대중음악\", \"복합\", \"서커스\", \"뮤지컬\"}")
    @PostMapping("/api/profiles/registration")
    public ResponseEntity<ProfileResponse> join(@RequestHeader("Authorization") String token,
                                                @RequestBody AddProfileRequest addProfileRequest) {
        // 기존 프로필 없을 경우
//        Profile newProfile = profileService.saveFirst(userId, addProfileRequest); //방법1
        Long memberId = memberService.getMemberIdByToken(token);
        Profile newProfile = profileService.saveFirst(memberRepository.findById(memberId).get(), addProfileRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.getProfile(newProfile.getId())); // 생성한 프로필 정보 응답
    }

    // 프로필등록 여부(O,X)
    @Operation(summary = "프로필 등록(온보딩 작성) 여부 체크 API", description = "토큰을 요청 받아 온보딩 작성한 회원이면 true, 아니면 false 리턴합니다.")
    @GetMapping("/api/profiles/registration/check")
    public ResponseEntity<ProfileRegistrationResponse> checkProfileRegistration(@RequestHeader("Authorization") String token) {
        Long memberId = memberService.getMemberIdByToken(token);
        Optional<Profile> profileOptional = profileRepository.findByMemberId(memberId);

        // 있을때
        if (profileOptional.isPresent()) {
            ResponseEntity.status(HttpStatus.OK).body(new ProfileRegistrationResponse(true));
        }
        // 없을때
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProfileRegistrationResponse(false));
    }

    // 닉네임 중복 체크
    @Operation(summary = "닉네임 중복 여부", description = "닉네임 중복이면 overlapping | 중복 아니면 non-overlapping 문자열 리턴")
    @GetMapping("/api/profiles/registration/{nickname}")
    public ResponseEntity<ProfileMessageResponse> isNicknameOverlap(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(profileService.isNicknameOverlap(nickname));
    }

    // 프로필 정보 반환
    @Operation(summary = "프로필 조회(by토큰) API", description = "토큰을 요청 받아 프로필 정보를 반환합니다.")
    @GetMapping("/api/profiles/myprofile")
    public ResponseEntity<ProfileResponse> getProfileByUserId(@RequestHeader("Authorization") String token){
        Long userId = memberService.getMemberIdByToken(token);
        ProfileResponse profileResponse = profileService.getProfileByUserId(userId);
        if (profileResponse == null) { // 프로필 작성이 없는 회원이면
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(profileResponse);
        }

        // 있으면 정보 리턴
        return ResponseEntity.status(HttpStatus.OK).body(profileResponse);
    }


    // 프로필 수정
    @Operation(summary = "프로필 수정", description = "프로필을 수정합니다.")
    @PostMapping("/updateProfile")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestHeader("Authorization") String token,
                                                         @RequestBody UpdateProfileRequest updateProfileRequest) {

        try {
            ProfileResponse updateProfile = profileService.updateProfile(token, updateProfileRequest);

            if (updateProfile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(updateProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
    }

//         ============================= 백엔드 테스트용: 1.토큰으로 유저 확인 2.로그인->프로필 등록 =============================
//    @PostMapping("/api/profile")
//    public ResponseEntity<?> addProfile(@RequestBody AddProfileRequest addProfileRequest,
//                                        Principal principal, @RequestHeader("Authorization") String token) {
//        log.info("/api/profile 토큰:" + token);
//        Long memberId = memberService.getMemberIdByToken(token);
//        String name = memberRepository.findById(memberId).get().getEmail();
//        log.info("현재 로그인한 사용자명(액세스토큰): " + name);
//        log.info("현재 로그인한 사용자명(principal): " + principal.getName());
//
//        // 프로필에 저장
////        addProfileRequest.profileGenre =
//        profileService.saveFirst(memberService.findById(memberId), addProfileRequest);
//
//        log.info("입력 받은 닉네임: " + addProfileRequest.profileNickname);
//        log.info("입력 받은 장르: " + addProfileRequest.profileGenre);
//
////        addProfileRequest.profileName = userRepository.findByEmail(principal.getName()).get().getNickname();
////        User user = userRepository.findByEmail(principal.getName()).get();
//////        Profile profile = profileService.saveProfile(addProfileRequest, user);
////        Profile profile = profileService.saveFirst(user, addProfileRequest);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(null);
//    }
}