package com.example.moro.app.post.controller;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.post.dto.CaptureRequest;
import com.example.moro.app.post.dto.CaptureResponse;
import com.example.moro.app.post.dto.LocationUpdateRequest;
import com.example.moro.app.post.dto.ShareResponse;
import com.example.moro.app.s3.S3Service;
import com.example.moro.app.post.dto.MainColorRequest;
import com.example.moro.app.post.dto.PostRequestDto;
import com.example.moro.app.post.dto.PostResponseDto;
import com.example.moro.app.post.service.PostService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import com.example.moro.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final S3Service s3Service;


    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponseTemplate<Void>> deletePost(@PathVariable Long postId, @AuthenticationPrincipal Member member) {

        postService.deletePost(postId, member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);
    }

    //게시물 공유
    @PostMapping("/{postId}/share")
    public ResponseEntity<ApiResponseTemplate<ShareResponse>> sharePost(@PathVariable Long postId, @AuthenticationPrincipal Member member) {
        ShareResponse response = postService.sharePost(postId, member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, response);
    }

    // ===== 홈 피드 스타일 게시물 조회 =====
    // 팔로우한 사용자들의 게시물 + 전체 공개 사용자의 게시물을 최신순으로 조회 (무한 스크롤 지원)
    @GetMapping("/feed")
    public ResponseEntity<ApiResponseTemplate<PageResponse<PostResponseDto>>> getHomeFeed(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20) Pageable pageable) {

        PageResponse<PostResponseDto> feed = postService.getHomeFeed(member.getId(), pageable);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, feed);
    }

    // 단일 게시물 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseTemplate<PostResponseDto>> getPost(@PathVariable Long postId) {
        PostResponseDto post = postService.getPost(postId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, post);
    }

    // ===== 단계별 게시물 생성 플로우 =====

    // 1. 사진 촬영 → 임시 게시물 생성 + 미리보기
    @PostMapping("/actions/capture")
    public ResponseEntity<ApiResponseTemplate<CaptureResponse>> capturePhoto(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "lat", required =false) Double lat,
            @RequestParam(value = "lng", required = false) Double lng,
            @AuthenticationPrincipal Member member) throws IOException {

        // S3에 이미지 업로드
        String imageUrl = s3Service.uploadImage(image);
        System.out.println("Uploaded image URL to S3: " + imageUrl); // S3 이미지 URL 로그 추가

        // 기존 로직에 S3 URL 적용
        CaptureRequest request = new CaptureRequest(imageUrl, lat, lng);
        CaptureResponse response = postService.createDraftFromCapture(request, member);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, response);
    }

    // 2. 재촬영 → 임시 게시물 완전 삭제
    // @DeleteMapping("/drafts/{draftId}/retake")
    // public ResponseEntity<ApiResponseTemplate<Void>> retakePhoto(
    //         @PathVariable Long draftId,
    //         @AuthenticationPrincipal Member member) {
    //
    //     postService.deleteDraftCompletely(draftId, member);
    //     return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);
    // }

    // 3. 위치 조정
    @PatchMapping("/drafts/{draftId}/location")
    public ResponseEntity<ApiResponseTemplate<Void>> updateLocation(
            @PathVariable Long draftId,
            @RequestBody LocationUpdateRequest request,
            @AuthenticationPrincipal Member member) {

        postService.updateDraftLocation(draftId, request, member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, null);
    }

    // 4. 대표 색상 선택 (상위 4개 중)
    @PatchMapping("/drafts/{draftId}/main-color")
    public ResponseEntity<ApiResponseTemplate<Void>> selectMainColor(
            @PathVariable Long draftId,
            @RequestBody MainColorRequest request,
            @AuthenticationPrincipal Member member) {

        postService.updateDraftMainColor(draftId, request.getSelectedColorId(), member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, null);
    }

    // 5. 최종 업로드 (DRAFT → PUBLISHED)
    @PostMapping("/drafts/{draftId}/publish")
    public ResponseEntity<ApiResponseTemplate<Long>> publishDraft(
            @PathVariable Long draftId,
            @AuthenticationPrincipal Member member) {

        Long publishedPostId = postService.publishDraft(draftId, member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, publishedPostId);
    }


    //게시물 생성 (단계별 게시물 생성만 존재해서 주석처리함)
    //@PostMapping
    //public ResponseEntity<ApiResponseTemplate<Long>> createPost(@RequestBody PostRequestDto requestDto,
    //                                                          @AuthenticationPrincipal Member member) {
    //    Long postId = postService.createPost(requestDto, member);
    //return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED,postId);
    //}

}
