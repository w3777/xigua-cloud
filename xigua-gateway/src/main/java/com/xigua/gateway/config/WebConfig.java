package com.xigua.gateway.config;

import com.xigua.gateway.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName WebConfig
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/28 16:41
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")   //默认对所有请求进行拦截
                .excludePathPatterns("/user/login");    //对login不拦截
    }
}
