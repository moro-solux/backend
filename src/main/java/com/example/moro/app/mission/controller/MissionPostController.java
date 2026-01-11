package com.example.moro.app.mission.controller;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.mission.dto.*;
import com.example.moro.app.mission.service.MisCommentService;
import com.example.moro.app.mission.service.MissionPostService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionPostController {

    private final MissionPostService missionPostService;
    private final MisCommentService missionCommentService;

    // < 미션 주제 조회 >
    @GetMapping("now")
    public ResponseEntity<ApiResponseTemplate<MissionSubjectResponse>> getMissionNow(){
        MissionSubjectResponse response = missionPostService.getSubject();
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // <미션 업로드>
    // POST 방식으로 사진과 데이터를 함께 받음
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseTemplate<MissionPostResponse>> uploadMissionPost(
            @RequestPart("image") MultipartFile image,
            @RequestPart("data") MissionPostRequest request
    ) {
        MissionPostResponse response = missionPostService.saveMissionPost(image, request);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, response);
    }

    // <미션 조회(자신)>
    @GetMapping("posts/me")
    public ResponseEntity<ApiResponseTemplate<List<MissionPostResponse>>> getMyPosts(
            @AuthenticationPrincipal Member member // 현재 사용자
    ){
        List<MissionPostResponse> response = missionPostService.getMyPosts(member.getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // <미션 조회(전체)>
    @GetMapping("posts")
    public ResponseEntity<ApiResponseTemplate<List<MissionPostResponse>>> getAllPosts(){
        List<MissionPostResponse> response = missionPostService.getAllPosts();
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // <미션 조회(친구)>
    @GetMapping("posts/friends")
    public ResponseEntity<ApiResponseTemplate<List<MissionPostResponse>>> getMyFreinds(
            @AuthenticationPrincipal Member member
    ){
        List<MissionPostResponse> response = missionPostService.getFriendPosts(member.getEmail());
        // 친구 없을 때 빈 리스트 반환
        if (response == null || response.isEmpty()) {
            return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
        }
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // <미션 게시물 삭제>
    @DeleteMapping("posts/{misPostId}/delete")
    public ResponseEntity<?> deleteMissionPost(
            @AuthenticationPrincipal Member member,
            @PathVariable Long misPostId
    ){
        missionPostService.deleteMissionPost(member.getEmail(), misPostId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, "해당 미션 게시물이 성공적으로 삭제되었습니다.");
    }

    // <특정 미션 게시물 모든 댓글 조회>
    @GetMapping("posts/{misPostId}/comments")
    public ResponseEntity<ApiResponseTemplate<List<MisCommentResponse>>> getComment(
            @PathVariable("misPostId") Long misPostId
    ){
        List<MisCommentResponse> response = missionCommentService.getMisComments(misPostId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }

    // < 댓글 작성 >
    @PostMapping("posts/comments")
    public ResponseEntity<ApiResponseTemplate<Long>> addComment(
            @AuthenticationPrincipal Member member,
            @RequestBody MisCommentRequest request
    ){
        Long misCommentId = missionCommentService.createMisComments(member.getEmail(), request);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, misCommentId);
    }

    // < 특정 댓글 수정 >
    @PatchMapping("posts/comments/{misCommentId}/edit")
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal Member member,
            @PathVariable("misCommentId") Long misCommentId,
            @RequestBody MisCommentUpdateRequest request
    ){
        missionCommentService.updateMisComments(member.getEmail(), misCommentId, request.newContent());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED, "댓글이 성공적으로 수정되었습니다.");
    }

    // < 특정 댓글 삭제 >
    @DeleteMapping("posts/comments/{misCommentId}/delete")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal Member member,
            @PathVariable("misCommentId") Long misCommentId
    ){
        missionCommentService.deleteMisComments(member.getEmail(), misCommentId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, "댓글이 성공적으로 삭제되었습니다.");
    }

    // < 미션 게시물 공유 >
    @GetMapping("posts/share/{misPostId}")
    public ResponseEntity<ApiResponseTemplate<MissionShareResponse>> getSharePosts(
            Long mistPostId
    ){
        MissionShareResponse response = missionPostService.generateShareUrl(mistPostId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, response);
    }
}
