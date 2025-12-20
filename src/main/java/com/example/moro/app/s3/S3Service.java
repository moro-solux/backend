package com.example.moro.app.s3;

import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;


    public S3Service(S3Client s3Client, @Value("${app.s3.bucket}") String bucketName,
                     @Value("${app.s3.region}") String region) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;

    }

    public String uploadImage(MultipartFile image){
        try{
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(image.getContentType())
                    .contentLength(image.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
        } catch (S3Exception e) {
            if (e.statusCode() == 403) {
                throw new BusinessException(ErrorCode.S3_ACCESS_DENIED);
            }
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED, e.awsErrorDetails().errorMessage());

        } catch (SdkClientException e) {
            throw new BusinessException(ErrorCode.S3_CONNECTION_FAILED);

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED, "파일 처리 중 오류가 발생했습니다.");
        }
    }


    public void deleteImage(String imageUrl){

        try {
            String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);

        } catch (S3Exception e) {
            if (e.statusCode() == 403) {
                throw new BusinessException(ErrorCode.S3_ACCESS_DENIED);
            }
            throw new BusinessException(ErrorCode.S3_DELETE_FAILED);

        } catch (SdkClientException e) {
            throw new BusinessException(ErrorCode.S3_CONNECTION_FAILED);
        }
    }

    public String getImageUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

}
