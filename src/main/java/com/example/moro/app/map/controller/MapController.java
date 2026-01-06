package com.example.moro.app.map.controller;

import com.example.moro.app.map.dto.MapPostDetailResponse;
import com.example.moro.app.map.dto.MapPostSummary;
import com.example.moro.app.map.service.MapService;
import com.example.moro.app.member.entity.Member;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import com.example.moro.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Tag(name = "Maps", description = "지도 API")
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;
    private final SecurityUtil securityUtil;


    @Operation(summary = "지도 내 위치 기준 조회", description = "현재 내 위치(위도, 경도)를 기준으로 반경(radius) 내의 게시물을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponseTemplate<List<MapPostSummary>>> getMapByLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius)
    {
        Member me = securityUtil.getCurrentMember();

        return ApiResponseTemplate.success(
                SuccessCode.OPERATION_SUCCESSFUL, mapService.getPostsByLocation(me.getId(), lat, lng, radius)
        );
    }

    @Operation(summary = "지도 검색 조회", description = "특정 키워드로 장소나 게시물을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseTemplate<List<MapPostSummary>>> searchMap(@RequestParam String keyword, @RequestParam double radius)
    {
        Member me = securityUtil.getCurrentMember();

        return ApiResponseTemplate.success(
                SuccessCode.OPERATION_SUCCESSFUL, mapService.searchPostsByKeyword(me.getId(), keyword, radius)
        );
    }

    @Operation(summary = "지도 게시물 상세 조회", description = "지도 위의 마커(게시물)를 클릭했을 때 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseTemplate<MapPostDetailResponse>> getPostDetail(@PathVariable Long postId)
    {
        Member me = securityUtil.getCurrentMember();

        return ApiResponseTemplate.success(
                SuccessCode.OPERATION_SUCCESSFUL, mapService.getPostDetail(me.getId(), postId)
        );
    }


}
