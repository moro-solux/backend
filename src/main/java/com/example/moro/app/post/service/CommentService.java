package com.example.moro.app.post.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.post.dto.CommentResponseDto;
import com.example.moro.app.post.entity.Comment;
import com.example.moro.app.post.entity.Post;
import com.example.moro.app.post.repository.CommentRepository;
import com.example.moro.app.post.repository.PostRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    //댓글 생성
    public Long createComment(Long postId, String content, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물. id=" + postId));

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment).getId();

    }

    // 게시물별 댓글 목록 조회
    @Transactional
    public List<CommentResponseDto> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("게시물 없음"));

        //리파지토리에서 댓글리스트를 가져와서 dto 리스트로 변환
        return commentRepository.findByPostOrderByCreatedAtAsc(post).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

}

// 유저 엔티티에 사진 추가되면 여기에도 추가하기!!