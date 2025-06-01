package com.xigua.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName MinioProperties
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/8 13:59
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String proxy;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
