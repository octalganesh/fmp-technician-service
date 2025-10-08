package com.octal.fsm.service;
import com.octal.fsm.utils.TextUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;

@Service
public class S3PresignedUrlService {

    private final S3Presigner presigner;
    private final String bucketName = "yutka-fence";

    public S3PresignedUrlService() {
        this.presigner = S3Presigner.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("AKIA3FLD6ARUFCC532BI", "c9CloTw0jHyjxnc1eME4EaNUqZSDGhWO6NSjlIhN")
                ))
                .build();
    }

    public String generatePresignedUrl(String path,String contentType) {
        try {
            String extension = getExtensionFromContentType(contentType);
            String objectKey = !TextUtils.isEmpty(path) ? path : "default/" + System.currentTimeMillis() + "-file." + extension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(r -> r
                    .signatureDuration(Duration.ofHours(1))
                    .putObjectRequest(putObjectRequest)
            );

            URL url = presignedRequest.url();
            return url.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }

    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) return "bin";
        switch (contentType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "video/mp4":
                return "mp4";
            case "application/pdf":
                return "pdf";
            default:
                return "bin";
        }
    }
}
