package com.example.moro.global.config;

import io.swagger.v3.oas.models.info.Contact;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Moro API")
                        .version("V1.0")
                        .description("[솔룩스 MORO팀 백엔드]api 문서입니다.")
                        .contact(new Contact()
                                .url("https://github.com/moro-solux/backend")
                                .name("Solux.MORO"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ));
    }
}
