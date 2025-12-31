package com.example.moro.app.colormap.controller;

import com.example.moro.app.colormap.dto.*;
import com.example.moro.app.colormap.service.ColorMapService;
import com.example.moro.app.post.repository.PostRepository;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import com.example.moro.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/colormaps")
@RequiredArgsConstructor
public class ColorMapController {
    private final ColorMapService colorMapService;

    // 1. 컬러맵  조회 (기본)
    @GetMapping("")
    public ResponseEntity<ApiResponseTemplate<List<ThemeGroupResponse>>> getMyColorMaps(
            @AuthenticationPrincipal String email
    ){
        // 현재 로그인한 사용자 ID
        List<ThemeGroupResponse> response = colorMapService.getUserColorMaps(email);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // 2. 테마별 컬러맵 조회
    @GetMapping("/themes/{themeName}")
    public ResponseEntity<ApiResponseTemplate<ThemeGroupResponse>> getMyColorMapsByTheme(
            @AuthenticationPrincipal String email,
            @PathVariable String themeName
    ){
        // 특정 테마 정보만 조회
        ThemeGroupResponse response = colorMapService.getUserColorMapsByTheme(email, themeName);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // 2. 특정 색상의 사진들 조회
    @GetMapping("/colors/{colorId}/posts")
    public ResponseEntity<ApiResponseTemplate<PageResponse<ColorPostResponse>>> getPostsByColor(
            @AuthenticationPrincipal String email,
            @PathVariable Integer colorId,
            Pageable pageable
    ){
        PageResponse<ColorPostResponse> response = colorMapService.getPostsByColor(email, colorId, pageable);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // 특정 색상의 게시물 자세히 보기
    @GetMapping("/colors/{colorId}/posts/{postId}")
    public ResponseEntity<ApiResponseTemplate<PostDetailResponse>> getPostDetail(
            @AuthenticationPrincipal String email,
            @PathVariable Integer colorId,
            @PathVariable Long postId
    ){
        PostDetailResponse response = colorMapService.getPostDetail(email,postId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }
    // 3. 게시물 대표색 변경
    /*@PatchMapping("/posts/{postId}/mainColor")
    public ResponseEntity<ApiResponseTemplate<UpdateMainColorResponse>> updateMainColor(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @RequestBody UpdateMainColorRequest request
    ){
        UpdateMainColorResponse response = colorMapService.updatePostMainColor(userId, postId, request.newColorId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }*/
}
