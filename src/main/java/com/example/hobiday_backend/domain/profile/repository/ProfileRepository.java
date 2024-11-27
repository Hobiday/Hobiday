package com.example.hobiday_backend.domain.profile.repository;

import com.example.hobiday_backend.domain.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
//    Optional<Profile> findById(Long id);
    Optional<Profile> findByMemberId(Long id);
    Optional<Profile> findByProfileNickname(String nickname);
}
