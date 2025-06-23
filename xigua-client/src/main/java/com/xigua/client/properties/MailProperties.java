package com.xigua.client.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName MailProperties
 * @Description
 * @Author wangjinfei
 * @Date 2025/6/23 15:35
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
    private String host;
    private String protocol;
    private String username;
    private String password;

    @PostConstruct
    public void checkProperties() {
        if (host == null || protocol == null || username == null || password == null) {
            log.error("--------->>>>>>> MailProperties 配置不完整");
        }
    }
}
