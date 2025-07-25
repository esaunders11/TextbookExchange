package com.esaunders.TextbookExchange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

/**
 * AWS S3 configuration for providing a singleton S3Client bean.
 * Loads credentials and region from application properties.
 * @author Ethan Saunders
 */
@Configuration
public class AwsS3Config {

    /** AWS access key. */
    @Value("${aws.accessKey}")
    private String accessKey;

    /** AWS secret key. */
    @Value("${aws.secretKey}")
    private String secretKey;

    /** AWS region. */
    @Value("${aws.region}")
    private String region;

    /**
     * Creates and configures an S3Client bean for AWS S3 operations.
     * @return the configured S3Client
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                    )
                )
                .build();
    }
}
