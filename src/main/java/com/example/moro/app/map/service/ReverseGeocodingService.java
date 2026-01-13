package com.example.moro.app.map.service;

import com.example.moro.app.map.dto.GetAddressResponse;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReverseGeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(ReverseGeocodingService.class);

    private final GeoApiContext geoApiContext;

    public GetAddressResponse getAddressByLatLng(double lat, double lng) {
        try {
            LatLng location = new LatLng(lat, lng);

            GeocodingResult[] resultsKo = GeocodingApi.reverseGeocode(geoApiContext, location)
                    .language("ko")
                    .await();

            GeocodingResult[] resultsEn = GeocodingApi.reverseGeocode(geoApiContext, location)
                    .language("en")
                    .await();

            if ((resultsKo == null || resultsKo.length == 0) ||
                    (resultsEn == null || resultsEn.length == 0)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 좌표를 주소로 변환할 수 없습니다.");
            }

            String addressKo = resultsKo[0].formattedAddress;
            String addressEn = resultsEn[0].formattedAddress;

            return new GetAddressResponse(addressKo, addressEn);

        } catch (BusinessException e) {
            logger.error("Reverse Geocoding 실패: lat={}, lng={}", lat, lng, e);
            throw e;

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "위치 변환 서비스에 일시적인 문제가 발생했습니다.");
        }
    }

}
