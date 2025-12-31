package com.example.moro.app.post.service;

import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ColorExtractService {

    /**
     * 이미지에서 빈도수가 높은 상위 4가지 색상을 추출합니다.
     * @param imageStream 이미지 파일의 입력 스트림
     * @return 상위 4개 색상의 RGB 정수 값 리스트
     */
    public List<Integer> extractTopColors(InputStream imageStream) {
        try {
            BufferedImage image = ImageIO.read(imageStream);
            if (image == null) return Collections.emptyList();

            Map<Integer, Integer> colorMap = new HashMap<>();
            int width = image.getWidth();
            int height = image.getHeight();

            // 1. 픽셀 순회 및 빈도수 카운팅 (성능을 위해 5픽셀 단위 샘플링)
            for (int x = 0; x < width; x += 5) {
                for (int y = 0; y < height; y += 5) {
                    int rgb = image.getRGB(x, y);
                    colorMap.put(rgb, colorMap.getOrDefault(rgb, 0) + 1);
                }
            }

            // 2. 빈도수 기준 내림차순 정렬 후 상위 4개 추출
            return colorMap.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .limit(4)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // 예외 발생 시 빈 리스트 반환 혹은 로그 기록
            return Collections.emptyList();
        }
    }
}