package com.yang.gatherfriendsback.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;

/**
 * 自定义 Swagger(OpenAPI) 接口文档配置
 */
@Configuration
@Profile({"dev", "test"}) // 只在开发/测试环境启用
public class SwaggerConfig {

    /**
     * OpenAPI 基本信息配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("聚友")
                        .description("聚友接口文档")
                        .version("1.0"));
    }

    /**
     * API 分组配置，指定扫描的 controller 包路径
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .packagesToScan("com.yang.gatherfriendsback.controller")
                .pathsToMatch("/**")
                .build();
    }
}
