package com.xigua.client.config;

import com.xigua.client.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MinioConfig
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/8 13:56
 */
@Configuration
public class MinioConfig {

    @Bean
    @ConditionalOnBean(MinioProperties.class)
    @ConditionalOnProperty(prefix = "minio", name = {"endpoint", "access-key", "secret-key", "bucket-name"})
    public MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}
