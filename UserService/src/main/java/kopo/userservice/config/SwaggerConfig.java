package kopo.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private Info apiInfo() {
        return new Info()
                .title("UserService") // 문서 제목
                .description("User Service Description!!") // 문서 설명
                .contact(new Contact().name("soyeon kim") // 명세서 작성자 정보
                        .email("soyeon96129@gmail.com")
                        .url("https://github.com/chloe-soyeon"))
                .license(new License()
                        .name("우장산 산신령들은 자유롭게 사용가능"))
                .version("1.0.0");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components()).info(apiInfo());
    }

}
