package com.example.moro.app.map.service;

import com.example.moro.app.map.dto.GetLatLngResponse;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;


@Service
@RequiredArgsConstructor
public class GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    private final GeoApiContext geoApiContext;

    public GetLatLngResponse getLatLngByAddress(String address) {
        try {
            String query = "대한민국 " + address;

            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, query)
                    .region("kr")
                    .await();

            if (results == null || results.length == 0) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 주소를 좌표로 변환할 수 없습니다.");
            }

            double latitude = results[0].geometry.location.lat;
            double longitude = results[0].geometry.location.lng;

            return new GetLatLngResponse(latitude, longitude);

        } catch (BusinessException e) {
            logger.error("Geocoding 실패: address={}", address, e);
            throw e;

        } catch (Exception e) {
            throw new BusinessException( ErrorCode.SERVICE_UNAVAILABLE, "위치 변환 서비스에 일시적인 문제가 발생했습니다.");

        }
    }
}
