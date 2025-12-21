package com.example.moro.app.follow;

import com.example.moro.app.follow.dto.FollowRequestDto;
import com.example.moro.app.follow.dto.FollowResponseDto;
import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.common.SuccessCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final MemberService memberService;


    @PostMapping
    public ResponseEntity<?> follow(@RequestBody FollowRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);
        Follow follow = followService.requestFollow(member.getId(), request.getFollowingId());

        FollowResponseDto response = new FollowResponseDto(follow.getFollowId(), follow.getStatus());

        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, response);
    }
}
