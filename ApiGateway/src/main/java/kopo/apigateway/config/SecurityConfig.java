package kopo.apigateway.config;

import kopo.apigateway.filter.JwtAuthenticationFilter;
import kopo.apigateway.hadler.AccessDeniedHandler;
import kopo.apigateway.hadler.LoginServerAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {

    private final AccessDeniedHandler accessDeniedHandler;
    private final LoginServerAuthenticationEntryPoint loginServerAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        log.info(this.getClass().getName() + ".filterChain Start!");
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler));
        http.exceptionHandling(e -> e.authenticationEntryPoint(loginServerAuthenticationEntryPoint));
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        http.authorizeExchange(authz -> authz
                .pathMatchers(
                        "/user/reg/**",      // 회원가입
                        "/login/**",
                        "/reg/**",
                        "/user/actuator/**", // ✅ 게이트웨이 경유 액추에이터
                        "/actuator/**",
                        "/swagger-ui/**", "/v3/api-docs/**"
                ).permitAll()
                .pathMatchers("/user/**").hasAnyAuthority("ROLE_USER")
                .anyExchange().permitAll()
        );
        http.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        log.info(this.getClass().getName() + ".filterChain End!");
        return http.build();
    }

    /**
     * CORS 설정을 위한 Bean (프론트 서버에서 API 호출 가능하도록 허용)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:14000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
