package com.example.moro.app.mission.service;

import com.example.moro.app.follow.repository.FollowRepository;
import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.mission.dto.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.IIOException;

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
    private final ColorAnalysisService colorAnalysisService;

    // 미션 공유
    @Value("${app.base-url}")
    private String baseUrl;

    // < 미션 주제 조회 >
    @Transactional(readOnly = true)
    public MissionSubjectResponse getSubject(){
        // 가장 최근에 생성된 미션 조회
        Mission mission = missionRepository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "미션을 찾을 수 없습니다."));

        // 현재 시각 기준 오전/오후 유효성 검증
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime missionTime = mission.getCreatedAt();

        // 날짜가 다르거나, 오전/오후 시간대가 일치하지 않으면 예외 발생
        if(!isSameTimeWindow(now, missionTime)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"현재 유효한 미션이 없습니다.");
        }
        return new MissionSubjectResponse(
                mission.getMissionId(),
                mission.getMissionTitle(),
                mission.getMissionType(),
                mission.getTargetColor(),
                mission.getCreatedAt()
        );
    }

    private boolean isSameTimeWindow(LocalDateTime now, LocalDateTime missionTime){
        // 날짜 같은지
        boolean isSameDay = now.toLocalDate().isEqual(missionTime.toLocalDate());
        // 오전 여부 확인
        boolean isNowMorning = now.getHour() < 12;
        boolean isMissionMorning = missionTime.getHour() < 12;

        return isSameDay && (isNowMorning == isMissionMorning);
    }

    @Transactional
    public MissionPostResponse saveMissionPost(MultipartFile image, MissionPostRequest request) {
        // 1. 이미지 저장 로직
        // 실제 이미지는 s3에 저장, DB에는 그 경로를 저장함
        String imageUrl = s3Service.uploadImage(image);

        // 2. 외래키 객체 조회
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        // Mission 테이블 참조
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("미션 찾을 수 없습니다."));

        // 미션 타입에 따른 결과 데이터 결정
        String missionDetailValue;
        if(Boolean.TRUE.equals(mission.getMissionType())){
            try{
                // 정확도 판별 미션일 경우 -> 색상 분석
                double score = colorAnalysisService.getMissionScore(image.getInputStream(), mission.getTargetColor());
                missionDetailValue = String.format("%.1f", score);
            }catch (IOException e){
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "이미지 분석 중 오류가 발생했습니다.");
            }
        } else{  // 일반 미션일 경우
            missionDetailValue = "-1";
        }

        // 3. 엔티티 생성 및 저장
        MissionPost missionPost = MissionPost.builder()
                .member(member)   // FK 연결
                .mission(mission)   // FK 연결
                .imageUrl(imageUrl)  // 저장된 사진 경로
                .detail(missionDetailValue)
                //.lat(request.getLat())
                //.lng(request.getLng())
                .createdAt(LocalDateTime.now())   // 생성 시간
                .build();

        MissionPost savedPost = missionPostRepository.save(missionPost);
        return MissionPostResponse.from(savedPost);
    }

    // 미션 게시글 조회(나)
    @Transactional(readOnly = true)
    public List<MissionPostResponse> getMyPosts(Long userId) {
        return missionPostRepository.findByMemberIdOrderByCreatedAtDesc(userId)
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
    public List<MissionPostResponse> getFriendPosts(String email){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"사용자를 찾을 수 없습니다."));

        // 1. 내가 '팔로워' 이고 상태가 'ACCEPTED'인 follow 객체 조회
        List<Follow> follows = followRepository.findByFollowerIdAndStatus(member.getId(), FollowStatus.ACCEPTED);

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
        return missionPostRepository.findByMemberIdInOrderByCreatedAtDesc(friendIds)
                .stream()
                .map(MissionPostResponse::from)
                .toList();
    }

    // 미션 게시물 삭제
    @Transactional
    public void deleteMissionPost(String email, Long misPostId){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        MissionPost post = missionPostRepository.findById(misPostId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "미션 게시물을 찾을 수 없습니다."));

        // 본인 확인
        if(!post.getMember().equals(member)){
            throw new BusinessException(ErrorCode.ACCESS_DENIED_EXCEPTION, "본인의 미션 게시물만 삭제할 수 있습니다.");
        }
        missionPostRepository.delete(post);
    }

    @Transactional
    public MissionShareResponse generateShareUrl(Long misPostId){

        // 게시물 존재 확인
        MissionPost post = missionPostRepository.findById(misPostId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "미션 게시물을 찾을 수 없습니다."));

        System.out.println("Current Base URL: " + baseUrl);
        // 공유 URL 생성
        String shareUrl = String.format("%s/share/missions/%d", baseUrl, misPostId);

        return new MissionShareResponse(misPostId, shareUrl);
    }

}
