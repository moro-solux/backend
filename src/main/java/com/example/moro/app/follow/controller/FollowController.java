package com.example.moro.app.follow.controller;

import com.example.moro.app.follow.service.FollowService;
import com.example.moro.app.follow.dto.FollowRequestDto;
import com.example.moro.app.follow.dto.FollowResponseDto;
import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.moro.global.util.SecurityUtil.getCurrentMember;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<ApiResponseTemplate<FollowResponseDto>> follow(@RequestBody FollowRequestDto request) {

        Member me = getCurrentMember();

        Follow follow = followService.requestFollow(me.getId(), request.getTargetUserId());

        FollowResponseDto response = new FollowResponseDto(follow.getFollowId(), follow.getStatus());

        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, response);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<ApiResponseTemplate<Void>> removeFollow(@PathVariable Long targetUserId) {

        Member member = getCurrentMember();
        followService.removeByFollower(member.getId(), targetUserId);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);
    }

    @PatchMapping("/{followId}/accept")
    public ResponseEntity<ApiResponseTemplate<FollowResponseDto>> acceptFollow(@PathVariable Long followId) {

        Member member = getCurrentMember();
        FollowResponseDto response = followService.approveFollow(followId, member.getId());

        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, response);
    }

    @DeleteMapping("/{followId}/reject")
    public ResponseEntity<ApiResponseTemplate<Void>> rejectFollow(@PathVariable Long followId) {
        Member me = getCurrentMember();
        followService.rejectByFollowing(followId, me.getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);
    }





}
