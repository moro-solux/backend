package com.example.moro.app.mission.service;

import org.springframework.stereotype.Service;

@Service
// CIELAB 색 공간 기반 유사도 계산
public class ColorSimilarityService {

    public double calculateSimilarity(String hex1, String hex2) {
        double[] lab1 = hexToLab(hex1);
        double[] lab2 = hexToLab(hex2);

        // Delta E -> 두 색상 사이 유클리드 거리 계산
        double deltaE = Math.sqrt(
                Math.pow(lab1[0] - lab2[0], 2) +
                        Math.pow(lab1[1] - lab2[1], 2) +
                        Math.pow(lab1[2] - lab2[2], 2)
        );

        // 점수 환산 로직
        //double score = 100.0 - deltaE;
        double score = 100.0 - (deltaE*0.8);

        // 범위 제한(0~100)
        return Math.max(0, Math.min(100, score));
    }

    private double[] hexToLab(String hex) {
        // hex 문자열 정규화
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        // hex -> RGB
        int r = Integer.valueOf(hex.substring(0, 2), 16);
        int g = Integer.valueOf(hex.substring(2, 4), 16);
        int b = Integer.valueOf(hex.substring(4, 6), 16);

        // RGB -> XYZ
        double[] xyz = rgbToXyz(r, g, b);

        // XYZ -> Lab
        return xyzToLab(xyz[0], xyz[1], xyz[2]);
    }

    private double[] rgbToXyz(int r, int g, int b) {
        double vR = (r / 255.0);
        double vG = (g / 255.0);
        double vB = (b / 255.0);

        // 감마 보정
        vR = (vR > 0.04045) ? Math.pow((vR + 0.055) / 1.055, 2.4) : vR / 12.92;
        vG = (vG > 0.04045) ? Math.pow((vG + 0.055) / 1.055, 2.4) : vG / 12.92;
        vB = (vB > 0.04045) ? Math.pow((vB + 0.055) / 1.055, 2.4) : vB / 12.92;

        vR *= 100;
        vG *= 100;
        vB *= 100;

        // D65 광원 기준 변환 행렬
        double x = vR * 0.4124 + vG * 0.3576 + vB * 0.1805;
        double y = vR * 0.2126 + vG * 0.7152 + vB * 0.0722;
        double z = vR * 0.0193 + vG * 0.1192 + vB * 0.9505;
        return new double[]{x, y, z};
    }

    private double[] xyzToLab(double x, double y, double z) {
        double vX = x / 95.047;
        double vY = y / 100.000;
        double vZ = z / 108.883;

        vX = (vX > 0.008856) ? Math.pow(vX, 1.0/3.0) : (7.787 * vX) + (16.0/116.0);
        vY = (vY > 0.008856) ? Math.pow(vY, 1.0/3.0) : (7.787 * vY) + (16.0/116.0);
        vZ = (vZ > 0.008856) ? Math.pow(vZ, 1.0/3.0) : (7.787 * vZ) + (16.0/116.0);

        double l = (116 * vY) - 16;
        double a = 500 * (vX - vY);
        double b = 500 * (vY - vZ);
        return new double[]{l, a, b};
    }
}
