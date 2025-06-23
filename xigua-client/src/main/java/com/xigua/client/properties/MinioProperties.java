package com.xigua.client.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName MinioProperties
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/8 13:59
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String proxy;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @PostConstruct
    public void checkProperties() {
        if (endpoint == null || accessKey == null || secretKey == null || bucketName == null) {
            log.error("--------->>>>>>> MinioProperties 配置不完整");
        }

        if(proxy == null){
            proxy = endpoint;
        }
    }
}
