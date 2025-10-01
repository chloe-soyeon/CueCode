package kopo.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;


@Slf4j
@Configuration
public class RouteConfig {

    /**
     * Gateway 라우팅: Eureka 미사용 → 직접 URL 사용
     */
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // FrontUI가 제공하는 로그인 페이지로 GET 요청을 라우팅합니다.
                .route("front-ui-login-page", r -> r
                        .path("/login")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("http://localhost:14000"))

                // FrontUI가 제공하는 대시보드 페이지로 GET 요청을 라우팅합니다.
                .route("front-ui-dashboard", r -> r
                        .path("/dashboard")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("http://localhost:14000"))

                // /user/dashboard 경로도 프론트엔드로 라우팅 (정적 대시보드 페이지, 경로 리라이트)
                .route("front-ui-user-dashboard", r -> r
                        .path("/user/dashboard")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f.rewritePath("/user/dashboard", "/user/dashboard.html"))
                        .uri("http://localhost:14000"))

                // UserService가 처리하는 로그인 POST 요청을 라우팅합니다.
                .route("user-service-login-post", r -> r
                        .path("/login")
                        .and()
                        .method(HttpMethod.POST)
                        .uri("http://localhost:11000"))

                // 기존의 유저 등록, 조회 관련 라우팅은 유지합니다.
                .route("user-service-user", r -> r
                        .path("/user/**")
                        .uri("http://localhost:11000"))
                .route("user-service-reg", r -> r
                        .path("/reg/**")
                        .uri("http://localhost:11000"))

                // /patient/list 경로를 UserService로 프록시하는 라우팅 추가
                .route("user-service-patient-list", r -> r
                        .path("/patient/list")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("http://localhost:11000"))

                // /patient/detail 경로를 UserService로 프록시하는 라우팅 추가
                .route("user-service-patient-detail", r -> r
                        .path("/patient/detail")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("http://localhost:11000"))

                // /manager/addPatient 경로의 POST 요청을 UserService로 프록시하는 라우팅 추가
                .route("user-service-manager-add-patient", r -> r
                        .path("/manager/addPatient")
                        .and()
                        .method(HttpMethod.POST)
                        .uri("http://localhost:11000"))

                // /patient/update 경로의 POST 요청을 UserService로 프록시하는 라우팅 추가
                .route("user-service-patient-update", r -> r
                        .path("/patient/update")
                        .and()
                        .method(HttpMethod.POST)
                        .uri("http://localhost:11000"))
                .build();
    }
}
