package com.example.moro.app.colormap.entity;

import jakarta.persistence.Entity;

@Entity
public class ColorMap {
    private Long colorId;
    private String colorTheme;
    private String hexCode;
}
