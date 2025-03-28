package com.xigua.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName OpenAPIConfig
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/28 9:15
 */
@OpenAPIDefinition
@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Gateway Service")
                .description("API Gateway Service")
                .version("1.0.0"));
    }

    // demo的swagger路由转发配置
    @Bean
    public RouteLocator demoRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("demo-api", r -> r.path("/demo/v3/api-docs")
                        .filters(f -> f.rewritePath("/demo/(?<segment>.*)", "/demo/${segment}"))
                        .uri("http://127.0.0.1:8081"))
                .build();
    }

    @Bean
    public RouteLocator userRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-api", r -> r.path("/user/v3/api-docs")
                        .filters(f -> f.rewritePath("/user/(?<segment>.*)", "/user/${segment}"))
                        .uri("http://127.0.0.1:8082"))
                .build();
    }
}
