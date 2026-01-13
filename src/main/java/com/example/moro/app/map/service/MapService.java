package com.example.moro.app.map.service;

import com.example.moro.app.map.dto.*;
import com.example.moro.app.map.repository.MapRepository;
import com.example.moro.app.map.util.GeoUtils;
import com.example.moro.app.post.entity.Post;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapService {

    private final MapRepository mapRepository;
    private final GeocodingService geocodingService;



    public List<MapPostSummary> getPostsByLocation(Long memberId, double lat, double lng, double radiusKm) {

        if (radiusKm <= 0) {
            throw new BusinessException( ErrorCode.BAD_REQUEST, "반경은 0보다 커야 합니다.");
        }

        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new BusinessException( ErrorCode.BAD_REQUEST, "위도 또는 경도 값이 올바르지 않습니다.");
        }


        BoundingBox box = GeoUtils.calculateBoundingBox(lat, lng, radiusKm);

        return mapRepository
                .findByMember_IdAndLatBetweenAndLngBetween(
                        memberId,
                        box.getMinLat(),
                        box.getMaxLat(),
                        box.getMinLng(),
                        box.getMaxLng()
                )
                .stream()
                .map(post -> new MapPostSummary(post.getId()))
                .toList();
    }



    public MapSearchResponse searchPostsByKeyword(Long memberId, String keyword, double radiusKm) {

        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "검색어는 필수입니다.");
        }

        GetLatLngResponse latLng = geocodingService.getLatLngByAddress(keyword);

        if (latLng == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 주소를 찾을 수 없습니다.");
        }

        List<MapPostSummary> posts = getPostsByLocation(memberId, latLng.getLatitude(), latLng.getLongitude(), radiusKm);

        return new MapSearchResponse(latLng.getLatitude(), latLng.getLongitude(), posts);
    }

    public MapPostDetailResponse getPostDetail(Long memberId, Long postId) {

        Optional<Post> postOpt = mapRepository.findById(postId);
        Post post = postOpt.orElseThrow(() ->
                new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "게시글이 존재하지 않습니다. postId=" + postId)
        );

        if (!post.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "해당 게시글에 접근 권한이 없습니다. memberId=" + memberId);
        }

        List<String> hexCodes = new ArrayList<>(Collections.nCopies(4, null));

        for (int i = 0; i < Math.min(4, post.getPostColors().size()); i++) {
            hexCodes.set( i, post.getPostColors().get(i).getColormap().getHexCode());
        }

        return MapPostDetailResponse.builder()
                .postId(post.getId())
                .createdAt(formatCreatedAt(post.getCreatedAt()))
                .imageUrl(post.getImageUrl())
                .placeName(post.getAddress())
                .addressKo(post.getDetailAddressKo())
                .addressEn(post.getDetailAddressEn())
                .hexCode1(hexCodes.get(0))
                .hexCode2(hexCodes.get(1))
                .hexCode3(hexCodes.get(2))
                .hexCode4(hexCodes.get(3))
                .build();
    }

    private String formatCreatedAt(LocalDateTime createdAt) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yy.MM.dd.E", Locale.ENGLISH);
        return createdAt.format(formatter);
    }

}
