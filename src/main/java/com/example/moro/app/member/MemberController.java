package com.example.moro.app.member;

import com.example.moro.app.follow.FollowService;
import com.example.moro.app.member.dto.MemberSearchResponse;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.common.SuccessCode;

import com.example.moro.global.common.dto.PageResponse;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MemberController {

    private final MemberService memberService;
    private final FollowService followService;


    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowerList(@PathVariable Long userId) {

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, followService.getFollowerList(userId));
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<?> getFollowingList(@PathVariable Long userId) {
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, followService.getFollowingList(userId));

    }

    @GetMapping("/me/follow-requests")
    public ResponseEntity<?> getFollowRequestList() {
        Member me = getCurrentMember();
        Long userId = me.getId();
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, followService.getRequestList(userId));

    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMember(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("userName").ascending());
        Page<MemberSearchResponse> response = memberService.search(keyword, pageable);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, PageResponse.from(response));
    }


    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        return memberService.findByEmail(authentication.getName());
    }



}