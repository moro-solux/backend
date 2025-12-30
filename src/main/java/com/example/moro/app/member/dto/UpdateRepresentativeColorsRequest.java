package com.example.moro.app.member.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateRepresentativeColorsRequest {
    private List<Long> colorIds;
}
