package com.xbf8.secure_s3.s3;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xbf8.secure_s3.SS3Properties;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class RustFSConfig {
    private String url;
    private String accessKey;
    private String secretKey;
    private String region;
    public RustFSConfig(SS3Properties ss3Properties) {
        this.url = ss3Properties.rustfs().url();
        this.accessKey = ss3Properties.rustfs().accessKey();
        this.secretKey = ss3Properties.rustfs().secretKey();
        this.region = ss3Properties.rustfs().region();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .endpointOverride(URI.create(url))
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
            )
            .forcePathStyle(true)
            .build();
    }
}
