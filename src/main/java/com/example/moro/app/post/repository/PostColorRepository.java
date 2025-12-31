package com.example.moro.app.post.repository;

import com.example.moro.app.post.entity.PostColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostColorRepository extends JpaRepository<PostColor,Integer> {
    // 게시물에 연결된 모든 후보 색상 id 가져오기
    @Query("SELECT pc.colormap.colorId From PostColor pc WHERE pc.post.id = :postId")
    List<Long> findColorIdsByPostId(@Param("postId") Long postId);
}
