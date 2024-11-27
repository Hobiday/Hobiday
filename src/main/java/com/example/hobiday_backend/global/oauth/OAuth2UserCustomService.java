package com.example.hobiday_backend.global.oauth;

import com.example.hobiday_backend.domain.member.entity.Member;
import com.example.hobiday_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청을 바탕으로 사용자 정보를 담은 객체 반환.
        OAuth2User user = super.loadUser(userRequest);

//      나중에 추가 로그인 구현한다면 사용. UserAccount도 구현필요
//        OAuth2UserInfo oAuth2UserInfo = new KakaoUserInfo((Map)user.getAttributes().get("kakao_account"),
//                String.valueOf(user.getAttributes().get("id")));

        Member memberEntity = save(user);
        return new PrincipalDetails(memberEntity, user.getAttributes());
    }

    // users 테이블에 사용자 정보가 없으면 회원 데이터 생성
    private Member save(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // OAuth2-client가 지원해주는 provider(구글, 페이스북등)는 Map형태로 key,Object로 반환해준다.
        // OAuth2-client가 지원해주지 않은 provider(네이버, 카카오톡)는 json형태로 반환해준다
        // OAuth핸들러에서도 onAuthenticationSuccess 내부 user에서 정보찾는 코드도 같이 수정 반영함
        Map attributesProperties = (Map) attributes.get("properties"); // 1. "properties" 상위분류에서 한번 더 거쳐서
        String nickname = (String) attributesProperties.get("nickname"); // 2. 사용자 정보 가져와야함.
        Map attributesKakaoAcount = (Map) attributes.get("kakao_account");
        String email = (String) attributesKakaoAcount.get("email");

//        log.info("OAuth2UserCustomService에서 nickname: {}, email: {}", nickname, email);

        Member member = memberRepository.findByEmail(email) // 기존 회원 여부 찾는 기준은 email
//                .map(entity -> entity.update(email)) // 업데이트는 안함(폼 로그인 or 소셜로그인 추가 시에 동일인 하나로 통합할때 필요 가능성)
                .orElse(Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .build());
        return memberRepository.save(member);
    }
}
