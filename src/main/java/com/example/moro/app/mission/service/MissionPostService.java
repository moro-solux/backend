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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end;

        // 현재 시간에 따라 조회 범위를 오늘 오전 또는 오늘 오후로 설정
        if (now.getHour() < 12) {
            // 오전: 00:00:00 ~ 11:59:59
            start = now.toLocalDate().atStartOfDay();
            end = now.toLocalDate().atTime(11, 59, 59);
        } else {
            // 오후: 12:00:00 ~ 23:59:59
            start = now.toLocalDate().atTime(12, 0, 0);
            end = now.toLocalDate().atTime(23, 59, 59);
        }

        // 해당 범위에 속하는 미션을 DB에서 직접 조회
        Mission mission = missionRepository.findByCreatedAtBetween(start, end)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "현재 시간대에 할당된 미션이 없습니다."));

        return new MissionSubjectResponse(
                mission.getMissionId(),
                mission.getMissionTitle(),
                mission.getMissionType(),
                mission.getTargetColor(),
                mission.getCreatedAt()
        );
    }

    // 이미지 색상 분석 프리뷰
    @Transactional(readOnly = true)
    public MissionAnalysisResponse analyzeImagePreview(MultipartFile image, Long missionId){
        // 1. 미션 정보 확인
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(()-> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "미션을 찾을 수 없습니다."));

        // 2. 정확도 판별 미션인지 확인
        if(Boolean.FALSE.equals(mission.getMissionType())){
            return new MissionAnalysisResponse(-1.0); // 일반 미션인 경우
        }

        try{
            // 3. 색상 분석
            double rawScore = colorAnalysisService.getMissionScore(image.getInputStream(), mission.getTargetColor());

            // 4. 소수점 첫째 자리까지 반올림 (선택 사항)
            double roundedScore = Math.round(rawScore * 10.0) / 10.0;

            return new MissionAnalysisResponse(roundedScore);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,"이미지 분석 중 오류각 발생했습니다.");
        }
    }

    @Transactional
    public MissionPostResponse saveMissionPost(MultipartFile image, MissionPostRequest request, Member member) {
        // 1. 이미지 저장 로직
        // 실제 이미지는 s3에 저장, DB에는 그 경로를 저장함
        String imageUrl = s3Service.uploadImage(image);

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
        return missionPostRepository.findPublicPostsOfLatestMission()//findAllByOrderByRandom()
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

    // 내 특정 미션 게시물 조회
    @Transactional(readOnly = true)
    public MissionPostResponse viewMissionPostsByMissionId(String email, Long misPostId){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        MissionPost post = missionPostRepository.findById(misPostId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "미션 게시물을 찾을 수 없습니다."));

        // 3. DTO로 변환하여 반환
        return MissionPostResponse.from(post);
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
