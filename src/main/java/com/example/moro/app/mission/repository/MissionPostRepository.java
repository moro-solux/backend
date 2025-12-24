package com.example.moro.app.mission.repository;

import com.example.moro.app.mission.entity.MissionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionPostRepository extends JpaRepository<MissionPost,Long> {

    // 1. 내가 올린 미션 게시글 조회(최신순)
    List<MissionPost> findByMember_IdOrderByCreatedAtDesc(Long memberId);

    // 2. 전체 사용자 미션 게시글 조회(최신순)
    List<MissionPost> findAllByOrderByCreatedAtDesc();

    // 3. 팔로워들의 미션 게시글 조회(최신순)

}
