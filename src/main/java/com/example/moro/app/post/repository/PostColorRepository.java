package com.example.moro.app.post.repository;

import com.example.moro.app.post.entity.PostColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostColorRepository extends JpaRepository<PostColor,Integer> {

    @Query("SELECT pc FROM PostColor pc JOIN FETCH pc.colormap WHERE pc.post.id = :postId")
    List<PostColor> findAllByPostId(@Param("postId") Long postId);
}
