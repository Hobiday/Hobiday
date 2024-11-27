package com.example.hobiday_backend.domain.member.entity;

import com.example.hobiday_backend.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Member {
    //사용자의 인증 정보와 권한 정보를 저장하는 메서드 제공
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Profile profile;

    @Column(name = "password")
    private String password;

    // 사용자 이메일(고유)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // 사용자 이름
    @Column(name="nickname")
    private String nickname;

    // 생성자에 nickname 추가 (위의 기존 생성자는 주석처리)
    @Builder
    public Member(String password, String email, String nickname){
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

}