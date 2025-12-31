package com.example.moro.app.post.controller;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.post.dto.CommentRequestDto;
import com.example.moro.app.post.dto.CommentResponseDto;
import com.example.moro.app.post.service.CommentService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<ApiResponseTemplate<Long>> createComment(@PathVariable Long postId,
                                                   @RequestBody CommentRequestDto requestDto,
                                                   @AuthenticationPrincipal Member member) {
        Long commentId = commentService.createComment(postId, requestDto.getContent(), member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, commentId);
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponseTemplate<List<CommentResponseDto>>> getComments(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getComments(postId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, comments);
    }
}
