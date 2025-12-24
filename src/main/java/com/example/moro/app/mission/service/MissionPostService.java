package com.example.moro.app.mission.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.mission.dto.MissionPostRequest;
import com.example.moro.app.mission.entity.Mission;
import com.example.moro.app.mission.entity.MissionPost;
import com.example.moro.app.mission.repository.MemberRepository;
import com.example.moro.app.mission.repository.MissionPostRepository;
import com.example.moro.app.mission.repository.MissionRepository;
import com.example.moro.app.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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
}
