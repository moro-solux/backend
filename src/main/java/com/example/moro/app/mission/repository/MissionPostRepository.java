package com.example.moro.app.mission.repository;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.mission.entity.Mission;
import com.example.moro.app.mission.entity.MissionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionPostRepository extends JpaRepository<MissionPost,Long> {

    // 1. 내가 올린 미션 게시글 조회(최신순)
    List<MissionPost> findByMemberIdOrderByCreatedAtDesc(Long userId);

    // 2. 전체 사용자 미션 게시글 조회(랜덤)
    //@Query(value = "SELECT * FROM mission_post ORDER BY RAND()", nativeQuery = true)
    //List<MissionPost> findAllByOrderByRandom();

    // 공개 계정인 사용자 게시물만 전체 조회
    @Query(value = """
    SELECT mp.* FROM mission_post mp
    JOIN user u ON mp.user_id = u.id
    WHERE u.is_public = true
    ORDER BY RAND()
    """, nativeQuery = true)
    List<MissionPost> findAllPublicPostOrderByRandom();

    // 3. 팔로워들의 미션 게시글 조회(최신순)
    List<MissionPost> findByMemberIdInOrderByCreatedAtDesc(List<Long> memberIds);

}
