package com.example.moro.app.mission.repository;

import com.example.moro.app.mission.entity.MissionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionPostRepository extends JpaRepository<MissionPost,Long> {
}
