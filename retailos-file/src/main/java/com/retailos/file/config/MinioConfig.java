package com.retailos.file.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${retailos.minio.url:http://localhost:9000}")
    private String url;

    @Value("${retailos.minio.access-key:retailos}")
    private String accessKey;

    @Value("${retailos.minio.secret-key:retailos123}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
