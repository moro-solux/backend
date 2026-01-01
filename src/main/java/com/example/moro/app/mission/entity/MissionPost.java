package com.example.moro.app.mission.entity;

import com.example.moro.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MissionPost {

    @Id
    @GeneratedValue
    private Long misPostId;  // 미션 게시물 id

    // Mission 테이블과 연결: 여러 개의 포스트는 하나의 미션에 속함 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "mission_id")  // DB의 외래키 컬럼명과 매칭
    private Mission mission;

    // User 테이블과 관계 설정 필요함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    private LocalDateTime createdAt; // 생성일
    private String imageUrl;   // 이미지
    private String detail;   // 상세

    private Double lat;   // 위도  // 안쓸듯
    private Double lng;   // 경도

}
