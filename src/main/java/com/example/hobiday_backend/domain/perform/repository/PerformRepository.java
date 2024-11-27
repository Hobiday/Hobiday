package com.example.hobiday_backend.domain.perform.repository;

import com.example.hobiday_backend.domain.perform.entity.Perform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformRepository extends JpaRepository<Perform, Long> {
//    Optional<PerformanceRepository> findById(Long id);
}