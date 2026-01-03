package com.example.moro.app.mission.repository;

import com.example.moro.app.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission,Long> {
    // 미션 조회
    Optional<Mission> findFirstByOrderByCreatedAtDesc();
}
