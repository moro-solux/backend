package com.example.moro.app.post.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.notification.service.NotificationService;
import com.example.moro.app.post.dto.CommentResponseDto;
import com.example.moro.app.post.entity.Comment;
import com.example.moro.app.post.entity.Post;
import com.example.moro.app.post.repository.CommentRepository;
import com.example.moro.app.post.repository.PostRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
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
    private final NotificationService notificationService;


    //댓글 생성
    public Long createComment(Long postId, String content, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"존재하지 않는 게시물. id=" + postId));

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);


        /* 알림: 댓글 작성자가 게시물 본인이 아닐 경우 알림 발송 */
        Member receiver = post.getMember();

        if (!post.getMember().getId().equals(member.getId()) && Boolean.TRUE.equals(receiver.getIsNotification())) {
            notificationService.notifyComment(
                    post.getMember().getId(),
                    member.getId(),
                    member.getUserName(),
                    "POST",
                    post.getId(),
                    savedComment.getId(),
                    content
            );
        }

        return savedComment.getId();

    }

    // 게시물별 댓글 목록 조회
    @Transactional
    public List<CommentResponseDto> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"게시물 없음"));

        //리파지토리에서 댓글리스트를 가져와서 dto 리스트로 변환
        return commentRepository.findByPostOrderByCreatedAtAsc(post).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

}

// 유저 엔티티에 사진 추가되면 여기에도 추가하기!!