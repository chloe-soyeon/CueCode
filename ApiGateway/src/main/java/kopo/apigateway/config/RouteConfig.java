package kopo.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RouteConfig {

    /**
     * Gateway 라우팅: Eureka 미사용 → 직접 URL 사용
     */
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-user", r -> r
                        .path("/user/**")
                        .uri("http://localhost:11000"))   // ← User-Service 실제 포트로 교체
                .route("user-service-login", r -> r
                        .path("/login/**")
                        .uri("http://localhost:11000"))
                .route("user-service-reg", r -> r
                        .path("/reg/**")
                        .uri("http://localhost:11000"))
                .build();
    }
}
