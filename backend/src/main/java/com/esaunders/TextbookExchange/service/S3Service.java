package com.esaunders.TextbookExchange.service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(S3Client s3Client, @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String keyName) throws Exception {
        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, keyName);
        } catch (S3Exception e) {
            // Log the error and rethrow as a generic exception or custom exception
            throw new Exception("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}