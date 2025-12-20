package com.example.moro.app.s3;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Tag(
        name = "S3 Test API",
        description = "⚠️ S3 연동 테스트를 위한 임시 API입니다. 실제 서비스 로직에서는 사용되지 않습니다."
)
@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadImage(image);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/image")
    public ResponseEntity<String> getImageUrl(@RequestParam String fileName) {
        String url = s3Service.getImageUrl(fileName);
        return ResponseEntity.ok(url);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestParam String imageUrl) {
        s3Service.deleteImage(imageUrl);
        return ResponseEntity.ok("삭제 완료");
    }
}
