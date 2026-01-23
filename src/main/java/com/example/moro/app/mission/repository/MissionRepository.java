package com.example.moro.app.mission.repository;

import com.example.moro.app.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission,Long> {
    // 미션 조회
    //Optional<Mission> findFirstByOrderByCreatedAtDesc();

    // MissionRepository.java
    Optional<Mission> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT * FROM mission m " +
            "WHERE DATE_ADD(m.created_at, INTERVAL 10 HOUR) <= NOW() " +
            "AND DATE_ADD(m.created_at, INTERVAL 12 HOUR) > NOW()",
            nativeQuery = true)
    List<Mission> findMissionsEndingInOneHour();

}
