package com.esaunders.TextbookExchange.service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

/**
 * Service for uploading files to AWS S3.
 * Handles file upload and S3 client configuration.
 * @author Ethan Saunders
 */
@Service
public class S3Service {
    /** S3 client for AWS operations. */
    private final S3Client s3Client;
    /** S3 bucket name. */
    private final String bucketName;

    /**
     * Constructs the S3Service with the given S3 client and bucket name.
     * @param s3Client the S3 client
     * @param bucketName the S3 bucket name
     */
    public S3Service(S3Client s3Client, @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    /**
     * Uploads a file to S3 and returns the public URL.
     * @param file the file to upload
     * @param keyName the S3 object key
     * @return the public URL of the uploaded file
     * @throws Exception if upload fails
     */
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