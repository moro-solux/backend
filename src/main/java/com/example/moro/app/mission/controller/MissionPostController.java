package com.example.moro.app.mission.controller;

import com.example.moro.app.mission.dto.MissionPostRequest;
import com.example.moro.app.mission.service.MissionPostService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/missions/upload")
@RequiredArgsConstructor
public class MissionPostController {

    private final MissionPostService missionPostService;

    // POST 방식으로 사진과 데이터를 함께 받음
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseTemplate<Long>> uploadMissionPost(
            @RequestPart("image") MultipartFile image,
            @RequestPart("data") MissionPostRequest request
    ) {
        Long savedId = missionPostService.saveMissionPost(image, request);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, savedId);
    }
}
