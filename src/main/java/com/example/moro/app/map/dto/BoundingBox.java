package com.example.moro.app.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoundingBox {
    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
}
