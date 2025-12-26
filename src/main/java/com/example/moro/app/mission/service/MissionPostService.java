package com.example.moro.app.mission.service;

import com.example.moro.app.follow.repository.FollowRepository;
import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.MemberRepository;
import com.example.moro.app.mission.dto.MissionPostRequest;
import com.example.moro.app.mission.dto.MissionPostResponse;
import com.example.moro.app.mission.entity.Mission;
import com.example.moro.app.mission.entity.MissionPost;
import com.example.moro.app.mission.repository.MissionPostRepository;
import com.example.moro.app.mission.repository.MissionRepository;
import com.example.moro.app.s3.S3Service;
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
        // 1. 현재 사용자 조회
        //Member me = memberRepository.findById(currentUserId)
        //        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 내가 '팔로워' 이고 상태가 'ACCEPTED'인 follow 객체 조회
        List<Follow> follows = followRepository.findByFollowerIdAndStatus(currentUserId, FollowStatus.ACCEPTED);

        // [추가] 친구 id 리스트 비었을 경우 빈 리스트 반환
        if (follows.isEmpty()) {
            return List.of();
        }

        // 3. Follow 객체에서 내가 팔로잉하는 상대방만 추출
        List<Long> friendIds = follows.stream()
                .map(follow -> follow.getFollowing().getId())
                .toList();

        // [추가] 친구 id 리스트 비었을 경우 빈 리스트 반환
        if (friendIds.isEmpty()) {
            return List.of();
        }

        // 4. 친구들이 작성한 게시글 조회
        return missionPostRepository.findByMember_IdInOrderByCreatedAtDesc(friendIds)
                .stream()
                .map(MissionPostResponse::from)
                .toList();
    }
}
