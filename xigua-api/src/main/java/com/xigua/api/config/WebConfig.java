package com.xigua.api.config;

import com.xigua.api.interceptor.LoginInterceptor;
import com.xigua.common.sequence.SequenceAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName WebConfig
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/28 16:41
 */
@ConditionalOnBean({SequenceAutoConfiguration.class})
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    private List<String> excludePathList = Arrays.asList(
            "/auth/login", "/auth/register", "/auth/logout", "/email/send",
            "/*/v3/api-docs/**", "/*/swagger-ui/**", "/*/swagger-ui.html");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")   //默认对所有请求进行拦截
                .excludePathPatterns(excludePathList);    //不拦截
    }
}
