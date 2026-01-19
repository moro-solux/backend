package com.example.moro.app.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetLatLngResponse {
    private double latitude;
    private double longitude;
}
