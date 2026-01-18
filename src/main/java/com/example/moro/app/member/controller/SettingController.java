package com.example.moro.app.member.controller;

import com.example.moro.app.member.dto.UpdateNotificationRequest;
import com.example.moro.app.member.dto.UpdatePublicRequest;
import com.example.moro.app.member.dto.UserNotificationStatus;
import com.example.moro.app.member.dto.UserPublicStatus;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberSettingService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings/")
public class SettingController {

    private final MemberSettingService memberSettingService;

    // 알림 상태
    @GetMapping("notification-status")
    public ResponseEntity<?> viewNotificationStatus(
            @AuthenticationPrincipal Member member
    ){
        UserNotificationStatus status = memberSettingService.viewNotificationStatus(member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, status);
    }

    @GetMapping("privacy-status")
    public ResponseEntity<?> viewPublicstatus(
            @AuthenticationPrincipal Member member
    ){
        UserPublicStatus status = memberSettingService.viewPublicStatus(member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, status);
    }

    // 알림 설정
    @PatchMapping("notification")
    public ResponseEntity<?> updateNotification(
            @AuthenticationPrincipal Member member,
            @RequestBody UpdateNotificationRequest request
    ){
        memberSettingService.updateNotification(member, request.isNotification());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, "알림 설정이 변경되었습니다.");
    }

    // 공개 설정
    @PatchMapping("privacy")
    public ResponseEntity<?> updatePrivacy(
            @AuthenticationPrincipal Member member,
            @RequestBody UpdatePublicRequest request
            ){
        memberSettingService.updatePublic(member, request.isPublic());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, "공개 여부 설정이 변경되었습니다.");

    }
}
