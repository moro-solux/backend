package com.example.moro.app.post.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.notification.service.NotificationService;
import com.example.moro.app.post.dto.PostLikeResponse;
import com.example.moro.app.post.entity.Like;
import com.example.moro.app.post.entity.Post;
import com.example.moro.app.post.repository.LikeRepository;
import com.example.moro.app.post.repository.PostRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    //좋아요 토글기능
    public void toggleLike(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"게시물 없음"));

        // 이미 좋아요를 눌렀는지 확인
        Optional<Like> postLike = likeRepository.findByPostAndMember(post, member);

        if (postLike.isPresent()) {
            likeRepository.delete(postLike.get()); // 있으면 취소
        } else {
            likeRepository.save(Like.builder() // 없으면 추가
                    .post(post)
                    .member(member)
                    .build());

            /* 알림 연결 : 자기 자신이 누른 좋아요를 제외한 알림 보내기 */
            Member receiver = post.getMember();

            if (!post.getMember().getId().equals(member.getId()) && Boolean.TRUE.equals(receiver.getIsNotification())) {
                notificationService.notifyLike(post.getMember().getId(), member.getId(), member.getUserName(), post.getId(), post.getImageUrl());
            }


        }
    }

    public PostLikeResponse getPostLikeInfo(Long postId) {
        long count = likeRepository.countByPostId(postId);

        PostLikeResponse.SimpleLikerInfo topLiker = null;

        if (count > 0) {
            Optional<Like> latestLike = likeRepository.findFirstByPostIdOrderByIdDesc(postId);
            if (latestLike.isPresent()) {
                Member member = latestLike.get().getMember();
                topLiker = new PostLikeResponse.SimpleLikerInfo(
                        member.getId(),
                        member.getUserName()
                );
            }
        }

        return PostLikeResponse.builder()
                .totalCount(count)
                .topLiker(topLiker)
                .build();
    }
}