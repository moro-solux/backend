package com.example.moro.app.map.dto;

import java.util.List;

public record MapSearchResponse(double centerLat, double centerLng, List<MapPostSummary> posts) {
}
