package com.example.moro.global.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ColorExtractor {

    // ===== 색상 분석 결과를 담는 DTO =====
    // 색상 ID와 실제 비율을 함께 저장하기 위한 클래스
    public static class ColorAnalysisResult {
        private final int colorId;
        private final double ratio;

        public ColorAnalysisResult(int colorId, double ratio) {
            this.colorId = colorId;
            this.ratio = ratio;
        }

        public int getColorId() { return colorId; }
        public double getRatio() { return ratio; }
    }

    public List<Integer> extractTop4Colors(String imageUrl) {
        try {
            // 분석 정확도 향상을 위한 리사이징 (더 큰 해상도)
            BufferedImage resizedImage = Thumbnails.of(new URL(imageUrl))
                    .size(600, 600)
                    .asBufferedImage();

            Map<Integer, Integer> idCounts = new HashMap<>();

            // 샘플링 적용: 8픽셀 단위로 분석하여 효율성 유지
            for (int y = 0; y < resizedImage.getHeight(); y += 8) {
                for (int x = 0; x < resizedImage.getWidth(); x += 8) {
                    int rgb = resizedImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    int nearestId = findNearestPaletteId(r, g, b);
                    idCounts.put(nearestId, idCounts.getOrDefault(nearestId, 0) + 1);
                }
            }

            // 빈도수 기준 내림차순 정렬 후 상위 4개 추출
            return idCounts.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .limit(4)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Arrays.asList(1, 2, 3, 4); // 에러 시 기본값
        }
    }

    // ===== 개선된 색상 분석 메서드 (비율 포함) =====
    // 기존 extractTop4Colors와 달리 실제 픽셀 비율을 계산해서 반환
    public List<ColorAnalysisResult> extractTop4ColorsWithRatio(String imageUrl) {
        try {
            // 분석 정확도 향상을 위한 리사이징 (더 큰 해상도)
            BufferedImage resizedImage = Thumbnails.of(new URL(imageUrl))
                    .size(600, 600)
                    .asBufferedImage();

            Map<Integer, Integer> idCounts = new HashMap<>();

            // 샘플링 적용: 8픽셀 단위로 분석하여 효율성 유지
            for (int y = 0; y < resizedImage.getHeight(); y += 8) {
                for (int x = 0; x < resizedImage.getWidth(); x += 8) {
                    int rgb = resizedImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    int nearestId = findNearestPaletteId(r, g, b);
                    idCounts.put(nearestId, idCounts.getOrDefault(nearestId, 0) + 1);
                }
            }

            // 샘플링된 픽셀 수 계산 (비율 계산을 위해)
            int totalPixels = (resizedImage.getWidth() / 8) * (resizedImage.getHeight() / 8);

            // 빈도수 기준 내림차순 정렬 후 상위 4개 추출 (비율 포함)
            return idCounts.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .limit(4)
                    .map(entry -> new ColorAnalysisResult(
                            entry.getKey(),
                            (double) entry.getValue() / totalPixels  // 실제 비율 계산
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // 예외 발생 시 상세 로그 추가
            System.err.println("ColorExtractor: !!! ERROR during image processing or URL access !!!");
            System.err.println("ColorExtractor: Image URL: " + imageUrl);
            System.err.println("ColorExtractor: Exception Type: " + e.getClass().getName());
            System.err.println("ColorExtractor: Exception Message: " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스 전체 출력

            // 에러 시 기본값 반환 (비율은 균등하게)
            return Arrays.asList(
                new ColorAnalysisResult(1, 0.25),
                new ColorAnalysisResult(2, 0.25),
                new ColorAnalysisResult(3, 0.25),
                new ColorAnalysisResult(4, 0.25)
            );
        }
    }

    //144개 중 해당 픽셀이 어떤 색과 가장 가까운지를 판별함. 그 색의 id를 반환
    private int findNearestPaletteId(int r, int g, int b) {
        ColorPalette nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (ColorPalette palette : ColorPalette.values()) {
            double distance = Math.sqrt(
                    Math.pow(palette.getR() - r, 2) +
                            Math.pow(palette.getG() - g, 2) +
                            Math.pow(palette.getB() - b, 2)
            );
            if (distance < minDistance) {
                minDistance = distance;
                nearest = palette;
            }
        }
        return nearest.getId();
    }
}