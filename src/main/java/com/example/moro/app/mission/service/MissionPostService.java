package com.example.moro.app.mission.service;

import com.example.moro.app.follow.repository.FollowRepository;
import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.mission.dto.MisCommentRequest;
import com.example.moro.app.mission.dto.MisCommentResponse;
import com.example.moro.app.mission.dto.MissionPostRequest;
import com.example.moro.app.mission.dto.MissionPostResponse;
import com.example.moro.app.mission.entity.MisComment;
import com.example.moro.app.mission.entity.Mission;
import com.example.moro.app.mission.entity.MissionPost;
import com.example.moro.app.mission.repository.MisCommentRepository;
import com.example.moro.app.mission.repository.MissionPostRepository;
import com.example.moro.app.mission.repository.MissionRepository;
import com.example.moro.app.s3.S3Service;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/*
*  이미지를 서버(s3)에 저장하고 그 결과인 url을 엔티티에 세팅함
* */

@Service
@RequiredArgsConstructor
public class MissionPostService {
    private final MissionPostRepository missionPostRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final FollowRepository followRepository;
    private final MisCommentRepository misCommentRepository;

    @Transactional
    public Long saveMissionPost(MultipartFile image, MissionPostRequest request) {
        // 1. 이미지 저장 로직
        // 실제 이미지는 s3에 저장, DB에는 그 경로를 저장함
        String imageUrl = s3Service.uploadImage(image);

        // 2. 외래키 객체 조회
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        // Mission 테이블 참조
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("미션 찾을 수 없습니다."));

        // 3. 엔티티 생성 및 저장
        MissionPost missionPost = MissionPost.builder()
                .member(member)   // FK 연결
                .mission(mission)   // FK 연결
                .imageUrl(imageUrl)  // 저장된 사진 경로
                .detail(request.getDetail())
                .lat(request.getLat())
                .lng(request.getLng())
                .createdAt(LocalDateTime.now())   // 생성 시간
                .build();

        return missionPostRepository.save(missionPost).getMisPostId();
    }

    // 미션 게시글 조회(나)
    @Transactional(readOnly = true)
    public List<MissionPostResponse> getMyPosts(Long userId){
        return missionPostRepository.findByMember_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(MissionPostResponse::from)
                .toList();
    }

    // 미션 게시글 조회(전체)
    @Transactional(readOnly = true)
    public List<MissionPostResponse> getAllPosts(){
        return missionPostRepository.findAllByOrderByRandom()
                .stream()
                .map(MissionPostResponse::from)
                .toList();
    }


    // 미션 게시물 조회(친구)
    @Transactional(readOnly = true)
    public List<MissionPostResponse> getFriendPosts(Long currentUserId){

        // 1. 내가 '팔로워' 이고 상태가 'ACCEPTED'인 follow 객체 조회
        List<Follow> follows = followRepository.findByFollowerIdAndStatus(currentUserId, FollowStatus.ACCEPTED);

        // [추가] 친구 id 리스트 비었을 경우 빈 리스트 반환
        if (follows.isEmpty()) {
            return List.of();
        }

        // 2. Follow 객체에서 내가 팔로잉하는 상대방만 추출
        List<Long> friendIds = follows.stream()
                .map(follow -> follow.getFollowing().getId())
                .toList();

        // [추가] 친구 id 리스트 비었을 경우 빈 리스트 반환
        if (friendIds.isEmpty()) {
            return List.of();
        }

        // 3. 친구들이 작성한 게시글 조회
        return missionPostRepository.findByMember_IdInOrderByCreatedAtDesc(friendIds)
                .stream()
                .map(MissionPostResponse::from)
                .toList();
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<MisCommentResponse> getMisComments(Long misPostId){

        // 모든 댓글 조회
        return misCommentRepository.findByMissionPost_MisPostIdOrderByMisCreatedAtAsc(misPostId)
                .stream()
                .map(comment -> new MisCommentResponse(
                        comment.getMisCommentId(),
                        comment.getMisContent(),
                        comment.getMissionPost().getMember().getUserName(),
                        comment.getMisCreatedAt()
                ))
                .toList();
    }

    // 댓글 생성
    @Transactional
    public Long createMisComments (String email, MisCommentRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"사용자를 찾을 수 없습니다."));

        MissionPost post = missionPostRepository.findById(request.misPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"미션 게시글을 찾을 수 없습니다."));

        MisComment comment = MisComment.builder()
                .missionPost(post)
                .member(member)
                .misContent(request.misContent())
                .misCreatedAt(LocalDateTime.now())
                .build();

        return misCommentRepository.save(comment).getMisCommentId();
    }

    // 댓글 수정
    @Transactional
    public void updateMisComments(String email, Long misCommentId, String newContent){
        MisComment comment = misCommentRepository.findById(misCommentId)
                .orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 본인 확인
        if(!comment.getMember().getEmail().equals(email)) {
            throw new RuntimeException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }
        comment.updateContent(newContent);
    }

    // 댓글 삭제
    @Transactional
    public void deleteMisComments(String email, Long misCommentId){
        MisComment comment = misCommentRepository.findById(misCommentId)
                .orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 본인 확인
        if(!comment.getMember().getEmail().equals(email)) {
            throw new RuntimeException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        misCommentRepository.delete(comment);
    }
}
