package com.xbf8.secure_s3.s3;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;
    public S3Service(
        S3Client s3Client,
        @Value("${secure_s3.rustfs.bucket-name}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void uploadStream(
        InputStream encryptInputStream,
        long contentLength,
        String objectKey
    ) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build();
        this.s3Client.putObject(
            putRequest,
            RequestBody.fromInputStream(encryptInputStream, contentLength)
        );
    }
}
