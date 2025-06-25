package com.planitsquare.holidaykeeper.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("HolidayKeeper API")
                .description("공휴일 관리 서비스 API 문서")
                .version("v1.0.0"));
    }
} 