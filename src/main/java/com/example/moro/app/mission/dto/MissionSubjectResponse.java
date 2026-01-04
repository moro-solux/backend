package com.example.moro.app.mission.dto;

import java.time.LocalDateTime;

public record MissionSubjectResponse(
        Long missionId,
        String missionTitle,
        Boolean missionType,
        String targetColor,
        LocalDateTime createdAt
){

}
