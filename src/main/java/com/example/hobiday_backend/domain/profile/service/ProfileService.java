package com.example.hobiday_backend.domain.profile.service;

import com.example.hobiday_backend.domain.profile.dto.request.AddProfileRequest;
import com.example.hobiday_backend.domain.profile.dto.request.UpdateProfileRequest;
import com.example.hobiday_backend.domain.profile.dto.response.ProfileMessageResponse;
import com.example.hobiday_backend.domain.profile.dto.response.ProfileRegistrationResponse;
import com.example.hobiday_backend.domain.profile.dto.response.ProfileResponse;
import com.example.hobiday_backend.domain.profile.entity.Profile;
import com.example.hobiday_backend.domain.profile.repository.ProfileRepository;
import com.example.hobiday_backend.domain.member.entity.Member;
import com.example.hobiday_backend.domain.member.repository.MemberRepository;
import com.example.hobiday_backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.hobiday_backend.domain.perform.util.GenreCasting.getGenreToList;
import static com.example.hobiday_backend.domain.perform.util.GenreCasting.getGenreToString;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;


    // 회원ID로 프로필 정보 반환
    public ProfileResponse getProfileByUserId(Long userid){
        Profile profile = profileRepository.findById(userid)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return ProfileResponse.builder()
                .profileId(profile.getId())
//                .userId(profile.getUserId()) // 방1
                .memberId(profile.getMember().getId()) // 방2
                .profileNickname(profile.getProfileNickname())
                .profileEmail(profile.getProfileEmail())
//                .profileGenre(profile.getProfileGenre())
                .build();
    }

    // 프로필ID로 프로필 정보 반환
    public ProfileResponse getProfile(Long profieId){
        Profile profile = profileRepository.findById(profieId).get();
        return ProfileResponse.builder()
                .profileId(profile.getId())
                .memberId(profile.getMember().getId())
                .profileNickname(profile.getProfileNickname())
                .profileEmail(profile.getProfileEmail())
//                .profileGenre(profile.getProfileGenre())
                .build();
    }

//    // 프로필 등록 여부. 없는 경우도 알 수 있게 프로필 리턴 -> 아직 테스트 미실행
//    public ProfileRegistrationResponse checkProfile(Long memberId){
//        Optional<Profile> profile = profileRepository.findById(); // 회원ID로 만들어진 프로필DB 있는지 확인
//
//        // 기존 회원일 경우
//        if (profile.isPresent()) {
//            return new ProfileRegistrationResponse(true);
//        }
//
//        // 신규 회원일 경우
//        return new ProfileRegistrationResponse(false);
//    }

    // 닉네임 중복 여부
    public ProfileMessageResponse isNicknameOverlap(String nickname){
        if (profileRepository.findByProfileNickname(nickname).isPresent()) { // 존재하는 닉네임이면
            return new ProfileMessageResponse("overlapping");
        }
        return new ProfileMessageResponse("non-overlapping");
    }

    // 프로필 등록(온보딩 작성)
    @Transactional
    public Profile saveFirst(//Long userId, //방1
                             Member member, //방2
                             AddProfileRequest addProfileRequest){
//        String email = userRepository.findById(userId).get().getEmail(); //방1
        String email = member.getEmail(); //방2
        Profile profile = profileRepository.save(Profile.builder()
//                .userId(userId) //방1
                .member(member) //방2
                .profileEmail(email)
                .profileNickname(addProfileRequest.profileNickname)
                .profileGenre(getGenreToString(addProfileRequest.profileGenre)) // 문자열 <- 리스트 변환해서 저장
                .build());
        profile.updateProfileActiveFlag(); // 프로필 등록 여부 true로 전환
        return profile;
    }

    // 프로필 업데이트
    public ProfileResponse updateProfile(String token, UpdateProfileRequest updateProfileRequest) {
        Long memberId = memberService.getMemberIdByToken(token);
        Profile profile = profileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId).get();
        // 사용자 일치 여부 확인
        if(!profile.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("프로필 수정 권한이 없습니다.");
        }
        profile = profileRepository.save(Profile.builder()
                .member(member)
                .profileEmail(profile.getProfileEmail())
                .profileNickname(profile.getProfileNickname())
                .profileGenre(!getGenreToString(updateProfileRequest.getProfileGenre()).isEmpty() ? getGenreToString(updateProfileRequest.getProfileGenre()) : profile.getProfileGenre())
                .profileIntroduction(updateProfileRequest.getProfileIntroduction() != null ? updateProfileRequest.getProfileIntroduction() : profile.getProfileIntroduction())
                .profileImageUrl(updateProfileRequest.getProfileImageUrl() != null ? updateProfileRequest.getProfileImageUrl() : profile.getProfileImageUrl())
                .build());

        profileRepository.save(profile);

        return ProfileResponse.builder()
                .profileId(profile.getId())
                .memberId(member.getId())
                .profileNickname(profile.getProfileNickname())
                .profileEmail(profile.getProfileEmail())
                .profileGenre(getGenreToList(profile.getProfileGenre()))
                .profileIntroduction(profile.getProfileIntroduction())
                .profileImageUrl(profile.getProfileImageUrl())
                .build();
    }


}