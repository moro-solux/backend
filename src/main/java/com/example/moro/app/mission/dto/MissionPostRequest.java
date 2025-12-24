package com.example.moro.app.mission.dto;

import lombok.Getter;
import lombok.Setter;

/*
* 앱에서 사진과 함께 보낸 데이터 담는 객체임
* */

@Getter
@Setter
public class MissionPostRequest {
    private Long missionId;  // 미션
    private Long userId;   // 작성자
    private String detail;   // 상세 내용
    private Double lat;  // 위도
    private Double lng;  // 경도
}
