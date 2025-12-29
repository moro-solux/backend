package com.example.moro.app.colormap.entity;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
public class UserColorMapId implements Serializable {
    private Long member;
    private Long colorMap;
}
