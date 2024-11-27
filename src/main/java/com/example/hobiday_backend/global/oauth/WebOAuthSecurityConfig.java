package com.example.hobiday_backend.global.oauth;

import com.example.hobiday_backend.domain.member.service.MemberService;
import com.example.hobiday_backend.global.jwt.RefreshTokenRepository;
import com.example.hobiday_backend.global.jwt.TokenAuthenticationFilter;
import com.example.hobiday_backend.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberService memberService;

    @Bean
    public WebSecurityCustomizer configure(){ // 스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring()
                //.requestMatchers(toH2Console())
                .requestMatchers(
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // 토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화
        return http
                .csrf(AbstractHttpConfigurer::disable) // 위조 요청 방지 (csrf토큰 포함되어야 요청 수락)
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화 -> 토큰 방식으로 대체
                .formLogin(AbstractHttpConfigurer::disable) // 폼로그인 비활성화 -> 토큰 방식으로 대체
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                ->세션매니지먼트(~Policy.STATELESS)는 백엔드 단독 실행에서의 확인용으로 꺼놓음
//                ->프론트로부터 받은 접속중인 토큰이 있어야 현재 로그인중인 사용자 정보를 백에서 사용 가능한데
//                ->프론트 코드가 없으니 일단 꺼놓음
//                ->세션매니먼트 키고 /api/profile 요청은 받아서 로그인중인 사용자정보 잘 받아오는거 확인 (profile.js로 프론트에서 토큰 전송)

                // 헤더를 확인할 커스텀 필터 추가
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll() // 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정.
                        .requestMatchers(new AntPathRequestMatcher("/api/test/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated() // 나머지 API URL은 인증 필요
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 미인증자일 경우 해당 페이지 호출
                        // Authorization 요청과 관련된 상태 저장
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        // 인증 성공시 실행할 핸들러
                        .successHandler(oAuth2SuccessHandler())
                )
                // '/api'로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        ))
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                memberService);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
