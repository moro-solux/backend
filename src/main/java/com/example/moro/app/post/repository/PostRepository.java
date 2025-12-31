package com.example.moro.app.post.repository;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    int countByMemberId(Long memberId);

    Page<Post> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    Page<Post> findByMemberAndMainColorIdInOrderByCreatedAtDesc(Member member, List<Integer> colorIds, Pageable pageable);

    Page<Post> findByMemberAndMainColorIdOrderByCreatedAtDesc(Member member, Integer colorId, Pageable pageable);
}
