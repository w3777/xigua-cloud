package com.xigua.client.config;

import com.xigua.client.properties.MailProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @ClassName MailConfig
 * @Description
 * @Author wangjinfei
 * @Date 2025/6/23 15:33
 */
@Configuration
public class MailConfig {
    @Bean
    @ConditionalOnProperty(prefix = "spring.mail",name = {"host", "username", "password"})
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setProtocol(mailProperties.getProtocol());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        return mailSender;
    }
}
