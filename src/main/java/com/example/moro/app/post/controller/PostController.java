package com.example.moro.app.post.controller;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.post.dto.PostRequestDto;
import com.example.moro.app.post.entity.Post;
import com.example.moro.app.post.repository.PostRepository;
import com.example.moro.app.post.service.PostService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    //게시물 생성
    @PostMapping
    public ResponseEntity<ApiResponseTemplate<Long>> createPost(@RequestBody PostRequestDto requestDto,
                                           @AuthenticationPrincipal Member member) {
        Long postId = postService.createPost(requestDto, member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED,postId);
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponseTemplate<Void>> deletePost(@PathVariable Long postId, @AuthenticationPrincipal Member member) {

        postService.deletePost(postId, member);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, null);
    }
}
