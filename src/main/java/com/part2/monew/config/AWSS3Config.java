package com.part2.monew.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AWSS3Config {

    @Value("${monew.storage.s3.accessKeyId}")
    private String accessKey;

    @Value("${monew.storage.s3.secretAccessKey}")
    private String secretKey;

    @Value("${monew.storage.s3.region}")
    private String region;

    @Value("${monew.storage.s3.bucket}")
    private String bucketName;

    @Value("${AWS_S3_PRESIGNED_URL_EXPIRATION:3600}")
    private long preSignedUrlExpiration;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    public Region awsRegion() {
        return Region.of(region);
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider, Region awsRegion) {
        return S3Client.builder().credentialsProvider(credentialsProvider).region(awsRegion)
            .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider, Region awsRegion) {
        return S3Presigner.builder().region(awsRegion).credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean
    @Qualifier("BucketName")
    public String s3BucketName() {
        return bucketName;
    }

    @Bean
    @Qualifier("PresignedUrlExpirationSeconds")
    public Long s3PresignedUrlExpirationSeconds() {
        return preSignedUrlExpiration;
    }
}
