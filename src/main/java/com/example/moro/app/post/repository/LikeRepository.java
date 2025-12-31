package com.example.moro.app.post.repository;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.post.entity.Like;
import com.example.moro.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {

    // 좋아요 토글 기능, 사용자와 게시물 정보 통해 좋아요 기록 찾기
    Optional<Like> findByPostAndMember(Post post, Member member);

    //게시물의 총 좋아요 개수 확인
    int countByPost(Post post);

    //사용자가 이미 좋아요를 눌렀는지 여부 확인
    boolean existsByPostAndMember(Post post, Member member);


}
