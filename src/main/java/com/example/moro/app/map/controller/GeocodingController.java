package com.example.moro.app.map.controller;

import com.example.moro.app.map.dto.GetLatLngResponse;
import com.example.moro.app.map.service.GeocodingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(
        name = "Geocoding Test API",
        description = "⚠️ geocoding api 정확도 테스트를 위한 임시 API입니다. 클라이언트 연동시 사용되지 않습니다."
)
@RestController
@RequestMapping("/api/geocode")
@RequiredArgsConstructor
public class GeocodingController {
    private final GeocodingService geocodingService;

    @GetMapping
    public ResponseEntity<GetLatLngResponse> getLatLng(
            @RequestParam String address
    ) {
        GetLatLngResponse result = geocodingService.getLatLngByAddress(address);
        return ResponseEntity.ok(result);
    }

}
