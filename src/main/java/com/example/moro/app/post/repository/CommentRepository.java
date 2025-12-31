package com.example.moro.app.post.repository;

import com.example.moro.app.post.entity.Comment;
import com.example.moro.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    //특정 게시물의 모든 댓글을 작성 순서대로 조회
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);


}
