package com.example.hobiday_backend.domain.member.repository;

import com.example.hobiday_backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
//    Optional<User> findByNickname(String nickname);
    Optional<Member> findByEmail(String email); // email로 사용자 정보 가져옴
}
