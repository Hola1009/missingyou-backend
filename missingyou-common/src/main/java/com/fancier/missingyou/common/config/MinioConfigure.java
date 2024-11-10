package com.fancier.missingyou.common.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Fancier
 * @version 1.0
 * @CreateTime: 2024-11-07
 * @Description:
 */
@Data
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "minio")
public class MinioConfigure {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    @Bean
    public MinioClient minioClient(){

        return MinioClient.builder()
                .endpoint(this.endpoint)
                .credentials(this.accessKey, this.secretKey)
                .build();
    }
}
