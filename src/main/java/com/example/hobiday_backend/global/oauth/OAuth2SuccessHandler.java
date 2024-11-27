package com.example.hobiday_backend.global.oauth;

import com.example.hobiday_backend.domain.member.entity.Member;
import com.example.hobiday_backend.domain.member.service.MemberService;
import com.example.hobiday_backend.global.jwt.RefreshToken;
import com.example.hobiday_backend.global.jwt.RefreshTokenRepository;
import com.example.hobiday_backend.global.jwt.TokenProvider;
import com.example.hobiday_backend.global.oauth.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(5); // 리프레시 토큰 기간 설정 5일
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2); // 액세스 토큰 기간 설정 2일
    public static final String REDIRECT_PATH = "http://localhost:3000/registration-form"; // 로그인 프로세스 모두 성공 후 리다이렉트할 페이지
//    public static final String REDIRECT_PATH = "/registration-form"; // 로그인 프로세스 모두 성공 후 리다이렉트할 페이지

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 카카오로 정보값 수정
        Map attributesKakaoAcount = (Map) attributes.get("kakao_account");
        String email = (String) attributesKakaoAcount.get("email");
        Member member = memberService.findByEmail(email);
        log.info("OAuth2SuccessHandler에서 email: {}", email);

        // 리프레시 토큰 생성 -> DB에 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        saveRefreshToken(member.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성 -> 패스에 엑세스 토큰 추가
        String accessToken = tokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken, refreshToken);

//        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 설정값과 쿠키 제거
        clearAuthenticationAttributes(request, response);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 생성된 리프레시 토큰을 전달받아 유저 아이디와 데이터베이스에 저장
    private void saveRefreshToken(Long memberId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId)
                .map(entity -> entity.update(newRefreshToken)) // 회원ID 대응되는 리프레시토큰 엔티티가 기존에 있으면 업데이트
                .orElse(new RefreshToken(memberId, newRefreshToken)); // 없으면 새로 생성
        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장.
    // 클라이언트에서 액세스 토큰이 만료되면 재발급 요청하도록 해당 메서드로 쿠키에 리프레시 토큰을 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME); // 백엔드 로컬로 실험하는 용도
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 액세스 토큰을 패스에 추가
    // 쿠키에서 리다이렉트 경로가 담긴 값을 가져와 쿼리 파라미터에 액세스 토큰을 추가한다
    // 액세스 토큰을 클라이언트에게 전달
    private String getTargetUrl(String access, String refresh) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("access", access)
                .queryParam("refresh", refresh)
                .build()
                .toUriString();
    }

    // 인증 관련 설정값과 쿠키 제거
    // 인증 프로세스를 진행하면서 세션과 쿠키에 임시로 저장해둔 인증 관련 데이터를 제거한다
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
