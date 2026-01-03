package com.example.moro.app.mission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionId;  // 미션 id

    private String missionTitle;   // 미션 제목

    private Boolean missionType; // True = 정확도 판별 미션, False = 그 외...

    private LocalDateTime createdAt;   // 미션 생성 시간


    // 양방향 설정: 하나의 미션은 여러 포스트를 가질 수 있음 (1:N)
    // 미션 객체에 해당하는 모든 포스트 목록 보고 싶을 떄를 위해 만듦.
    @OneToMany(mappedBy = "mission")
    private List<MissionPost> missionPosts = new ArrayList<>();
}
