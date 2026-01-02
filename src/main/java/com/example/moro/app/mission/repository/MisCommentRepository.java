package com.example.moro.app.mission.repository;

import com.example.moro.app.mission.entity.MisComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MisCommentRepository extends JpaRepository<MisComment, Long> {

    // 특정 게시물의 댓글을 생성일 순으로 조회
    List<MisComment> findByMissionPost_MisPostIdOrderByMisCreatedAtAsc(Long misPostId);
}
