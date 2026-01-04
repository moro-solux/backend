package com.example.moro.app.mission.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ColorAnalysisService {
    private final ColorSimilarityService similarityService;

    public ColorAnalysisService(ColorSimilarityService similarityService) {
        this.similarityService = similarityService;
    }

    public double getMissionScore(InputStream imageStream, String targetColor)
        throws IOException {
        // 성능을 위해 이미지 리사이징
        BufferedImage resizedImage = Thumbnails.of(imageStream)
                .size(100,100)
                .asBufferedImage();

        // K-Means 클러스터링
        List<String> dominantColors = extractDominantColors(resizedImage,5);

        // 추출된 색상등 중 타겟 색상과 가장 유사한 점수 찾기
        double bestScore = 0;
        for( String hex : dominantColors){
            double currentScore = similarityService.calculateSimilarity(hex, targetColor);
            if(currentScore > bestScore){
                bestScore = currentScore;
            }
        }
        return bestScore;
    }

    private List<String> extractDominantColors(BufferedImage image, int k) {
        int width = image.getWidth();
        int height = image.getHeight();
        List<Color> pixels = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels.add(new Color(image.getRGB(x, y)));
            }
        }

        // K-Means 알고리즘 적용
        List<Color> centroids = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < k; i++) {
            centroids.add(pixels.get(rand.nextInt(pixels.size())));
        }

        // 10회 반복
        for (int iteration = 0; iteration < 10; iteration++) {
            List<List<Color>> clusters = new ArrayList<>();
            for(int i=0; i<k; i++) clusters.add(new ArrayList<>());

            for (Color pixel: pixels) {
                int closestIndex =0;
                double minDistance = Double.MAX_VALUE;
                for(int i=0; i<k; i++){
                    double dist = Math.pow(pixel.getRed()- centroids.get(i).getRed(), 2) +
                            Math.pow(pixel.getGreen() - centroids.get(i).getGreen(), 2) +
                            Math.pow(pixel.getBlue() - centroids.get(i).getBlue(), 2);
                    if (dist < minDistance) {
                        minDistance = dist;
                        closestIndex = i;
                    }
                }
                clusters.get(closestIndex).add(pixel);
            }

            for (int i =0; i<k; i++){
                if (clusters.get(i).isEmpty()) continue;
                long r =0, g= 0, b = 0;
                for(Color c: clusters.get(i)){
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
                int size = clusters.get(i).size();
                centroids.set(i,new Color((int)(r/size),(int)(g/size),(int)(b/size)));
            }
        }
        return centroids.stream()
                .map(c -> String.format("#%02x%02x%02x", c.getRed(), c.getGreen(),c.getBlue()))
                .toList();
    }
}
