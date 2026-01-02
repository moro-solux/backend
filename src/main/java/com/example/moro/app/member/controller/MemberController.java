package com.example.moro.app.member.controller;

import com.example.moro.app.follow.service.FollowService;
import com.example.moro.app.member.dto.*;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;

import com.example.moro.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import static com.example.moro.global.util.SecurityUtil.getCurrentMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MemberController {

    private final MemberService memberService;
    private final FollowService followService;

    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowerList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponseTemplate.success(
                SuccessCode.RESOURCE_RETRIEVED,
                PageResponse.from(followService.getFollowers(userId, keyword, pageable))
        );
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<?> getFollowingList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponseTemplate.success(
                SuccessCode.RESOURCE_RETRIEVED,
                PageResponse.from(followService.getFollowings(userId, keyword, pageable)));
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

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId){

        Member currentUser = getCurrentMember();
        Long currentUserId = currentUser.getId();

        ProfileResponse response = memberService.getProfile(userId, currentUserId);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);

    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        Member me = getCurrentMember();
        memberService.updateProfile(
                me.getId(),
                request.getUserName(),
                request.getUserColorId(),
                request.getUserColorHex()
        );

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, "프로필이 성공적으로 업데이트되었습니다.");
    }

    @GetMapping("/{userId}/profile/feed")
    public ResponseEntity<?> getUserProfileFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "DEFAULT") ProfileFeedType viewType,
            @RequestParam(required = false) Integer colorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        UserFeedListResponse response = memberService.getProfileFeed(userId, viewType, colorId, pageable);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    @PutMapping("/me/colors/main")
    public ResponseEntity<?> updateRepresentativeColors(@RequestBody UpdateRepresentativeColorsRequest request) {

        Member me = getCurrentMember();
        memberService.updateRepresentativeColors(me.getId(), request.getColorIds());

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, "대표 색상이 성공적으로 변경되었습니다.");
    }



}