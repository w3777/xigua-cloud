package com.xigua.config;

import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.common.sequence.sequence.impl.SnowflakeSequence;
import com.xigua.interceptor.LoginInterceptor;
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
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private List<String> excludePathList = Arrays.asList("/user/login",
            "/*/v3/api-docs/**", "/*/swagger-ui/**", "/*/swagger-ui.html");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Sequence sequence = new SnowflakeSequence();
        registry.addInterceptor(new LoginInterceptor(sequence))
                .addPathPatterns("/**")   //默认对所有请求进行拦截
                .excludePathPatterns(excludePathList);    //不拦截
    }
}
