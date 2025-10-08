package com.octal.fsm.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.octal.fsm.utils.TextUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.net.URL;
import java.util.Date;

@Service
public class S3PresignedUrlService {

    private final AmazonS3 s3Client;
    private final String bucketName = "yutka-fence";

    public S3PresignedUrlService() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA3FLD6ARUFCC532BI", "c9CloTw0jHyjxnc1eME4EaNUqZSDGhWO6NSjlIhN");
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("ap-south-1")
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
    public String generatePresignedUrl(String path, String contentType) {
        try {
            // Generate a unique file name
            String extension = getExtensionFromContentType(contentType);
            String objectKey = !TextUtils.isEmpty(path) ? path : "/default/" + System.currentTimeMillis() + "-file." + extension;
            // Set expiration (1 hour)
            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60; // 1 hour
            expiration.setTime(expTimeMillis);
            // Generate pre-signed URL for PUT operation
            URL url = s3Client.generatePresignedUrl(
                    bucketName,
                    objectKey,
                    expiration,
                    HttpMethod.PUT
            );
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
