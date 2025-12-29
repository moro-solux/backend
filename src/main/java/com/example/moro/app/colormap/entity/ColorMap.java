package com.example.moro.app.colormap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ColorMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 자동으로 번호를 매김
    private Long colorId;
    private String colorTheme;
    private String hexCode;
}
