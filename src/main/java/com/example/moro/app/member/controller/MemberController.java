package com.example.moro.app.member.controller;

import com.example.moro.app.colormap.service.UserColorMapService;
import com.example.moro.app.follow.dto.FollowUserResponse;
import com.example.moro.app.follow.service.FollowService;
import com.example.moro.app.member.dto.*;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;

import com.example.moro.global.common.dto.PageResponse;
import com.example.moro.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Users", description = "회원 및 프로필 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MemberController {

    private final MemberService memberService;
    private final FollowService followService;
    private final UserColorMapService userColorMapService;
    private final SecurityUtil securityUtil;


    @Operation(summary = "팔로워 목록 조회", description = "특정 유저의 팔로워 목록을 조회합니다. (키워드를 통한 검색 가능)")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponseTemplate<PageResponse<FollowUserResponse>>> getFollowerList(
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

    @Operation(summary = "팔로잉 목록 조회", description = "특정 유저가 팔로잉하는 목록을 조회합니다. (키워드를 통한 검색 가능)")
    @GetMapping("/{userId}/followings")
    public ResponseEntity<ApiResponseTemplate<PageResponse<FollowUserResponse>>> getFollowingList(
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

    @Operation(summary = "팔로우 요청 받은 목록 조회", description = "나에게 들어온 팔로우 요청 목록을 조회합니다.")
    @GetMapping("/me/follow-requests")
    public ResponseEntity<ApiResponseTemplate<List<FollowUserResponse>>> getFollowRequestList() {
        Member me = securityUtil.getCurrentMember();
        Long userId = me.getId();
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, followService.getRequestList(userId));

    }

    @Operation(summary = "팔로워 삭제", description = "유저 본인의 팔로워 목록에서 특정 팔로워를 삭제합니다.")
    @DeleteMapping("/me/followers/{targetUserId}")
    public ResponseEntity<ApiResponseTemplate<Void>> removeFollower(@PathVariable Long targetUserId) {
        Member me = securityUtil.getCurrentMember();
        Long userId = me.getId();
        followService.removeFollower(userId, targetUserId);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);

    }

    @Operation(summary = "유저 검색", description = "전체 유저 중에서 키워드로 회원을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseTemplate<PageResponse<MemberSearchResponse>>> searchMember(
            @RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("userName").ascending());
        Page<MemberSearchResponse> response = memberService.search(keyword, pageable);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, PageResponse.from(response));
    }

    @Operation(summary = "프로필 조회 (색상 목록 포함)", description = "특정 유저의 프로필 정보(대표 색상 6가지 목록 포함)를 조회합니다.")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponseTemplate<ProfileResponse>> getUserProfile(@PathVariable Long userId) {

        Member currentUser = securityUtil.getCurrentMember();
        Long currentUserId = currentUser.getId();

        ProfileResponse response = memberService.getProfile(userId, currentUserId);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);

    }

    @Operation(summary = "프로필 수정", description = "나의 프로필 정보(이름, 프로필 배경색)를 수정합니다.")
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponseTemplate<String>> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        Member me = securityUtil.getCurrentMember();
        memberService.updateProfile(
                me.getId(),
                request.getUserName(),
                request.getUserColorId(),
                request.getUserColorHex()
        );

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, "프로필이 성공적으로 업데이트되었습니다.");
    }

    @Operation(summary = "프로필 피드 조회 (+필터별 조회)", description = "유저 프로필 하단의 피드를 조회합니다. (전체 보기, 대표 색상, 개별 색상) 등")
    @GetMapping("/{userId}/profile/feed")
    public ResponseEntity<ApiResponseTemplate<UserFeedListResponse>> getUserProfileFeed(
            @PathVariable Long userId,
            @Parameter(description = "조회 타입 (DEFAULT: 유저의 대표 색상, USER_COLORS: 색상 목록 6가지 동시 조회, SINGLE_COLOR: 색상 목록 내 개별 조회)")@RequestParam(defaultValue = "DEFAULT") ProfileFeedType viewType,
            @Parameter(description = "색상별 조회(SINGLE_COLOR) 시 필요한 색상 ID")@RequestParam(required = false) Integer colorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        UserFeedListResponse response = memberService.getProfileFeed(userId, viewType, colorId, pageable);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    @Operation(summary = "유저 대표 색상 목록 수정", description = "내 프로필에 표시되는 대표 색상 6가지를 수정합니다.")
    @PutMapping("/me/colors/main")
    public ResponseEntity<ApiResponseTemplate<String>> updateRepresentativeColors(
            @RequestBody UpdateRepresentativeColorsRequest request) {

        Member me = securityUtil.getCurrentMember();
        userColorMapService.updateRepresentativeColors(me.getId(), request.getColorIds());

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, "대표 색상이 성공적으로 변경되었습니다.");
    }



}