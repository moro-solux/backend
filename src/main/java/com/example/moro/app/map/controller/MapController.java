package com.example.moro.app.map.controller;

import com.example.moro.app.map.dto.MapPostDetailResponse;
import com.example.moro.app.map.dto.MapPostSummary;
import com.example.moro.app.map.service.MapService;
import com.example.moro.app.member.entity.Member;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.example.moro.global.util.SecurityUtil.getCurrentMember;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

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


}
