package com.example.moro.app.map.controller;


import com.example.moro.app.map.dto.MapPostDetailResponse;
import com.example.moro.app.map.dto.MapPostSummary;
import com.example.moro.app.map.service.MapService;
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

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponseTemplate<List<MapPostSummary>>> getMapByLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius)
    {
        Member me = getCurrentMember();

        return ApiResponseTemplate.success(
                SuccessCode.OPERATION_SUCCESSFUL, mapService.getPostsByLocation(me.getId(), lat, lng, radius)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseTemplate<List<MapPostSummary>>> searchMap(@RequestParam String keyword, @RequestParam double radius)
    {
        Member me = getCurrentMember();

        return ApiResponseTemplate.success(
                SuccessCode.OPERATION_SUCCESSFUL, mapService.searchPostsByKeyword(me.getId(), keyword, radius)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseTemplate<MapPostDetailResponse>> getPostDetail(@PathVariable Long postId)
    {
        Member me = getCurrentMember();

        return ApiResponseTemplate.success(
                SuccessCode.OPERATION_SUCCESSFUL, mapService.getPostDetail(me.getId(), postId)
        );
    }


    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        String email = authentication.getName();

        return memberService.findByEmail(email);
    }

}
