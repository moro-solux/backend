package com.example.moro.app.follow.controller;

import com.example.moro.app.follow.service.FollowService;
import com.example.moro.app.follow.dto.FollowRequestDto;
import com.example.moro.app.follow.dto.FollowResponseDto;
import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;

import com.example.moro.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Tag(name = "Follows", description = "팔로우 기능 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final SecurityUtil securityUtil;


    @Operation(summary = "팔로우 요청", description = "특정 유저에게 팔로우를 요청합니다.") // 2. API 제목 설정
    @PostMapping
    public ResponseEntity<ApiResponseTemplate<FollowResponseDto>> follow(@RequestBody FollowRequestDto request) {

        Member me = securityUtil.getCurrentMember();

        Follow follow = followService.requestFollow(me.getId(), request.getTargetUserId());

        FollowResponseDto response = new FollowResponseDto(follow.getFollowId(), follow.getStatus());

        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, response);
    }

    @Operation(summary = "팔로우 취소 및 언팔로우", description = "상대방에 대한 팔로우 요청을 취소하거나 언팔로우합니다.")
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<ApiResponseTemplate<Void>> removeFollow(@PathVariable Long targetUserId) {

        Member member = securityUtil.getCurrentMember();
        followService.removeByFollower(member.getId(), targetUserId);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);
    }

    @Operation(summary = "팔로우 승인", description = "나에게 온 팔로우 요청을 수락합니다.")
    @PatchMapping("/{followId}/accept")
    public ResponseEntity<ApiResponseTemplate<FollowResponseDto>> acceptFollow(@PathVariable Long followId) {

        Member member = securityUtil.getCurrentMember();
        FollowResponseDto response = followService.approveFollow(followId, member.getId());

        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, response);
    }

    @Operation(summary = "팔로우 거절", description = "나에게 온 팔로우 요청을 거절(삭제)합니다.")
    @DeleteMapping("/{followId}/reject")
    public ResponseEntity<ApiResponseTemplate<Void>> rejectFollow(@PathVariable Long followId) {
        Member me = securityUtil.getCurrentMember();
        followService.rejectByFollowing(followId, me.getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);
    }





}
