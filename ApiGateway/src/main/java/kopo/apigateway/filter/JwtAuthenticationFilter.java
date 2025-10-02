package kopo.apigateway.filter;

import kopo.apigateway.jwt.JwtStatus;
import kopo.apigateway.jwt.JwtTokenProvider;
import kopo.apigateway.jwt.JwtTokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = jwtTokenProvider.resolveToken(request, JwtTokenType.ACCESS_TOKEN);

        // 토큰이 존재하고, 유효성 검증에 성공한 경우
        if (token != null && jwtTokenProvider.validateToken(token) == JwtStatus.ACCESS) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            log.info("Authenticated user: {}, roles: {}", authentication.getName(), authentication.getAuthorities());

            // SecurityContext에 인증 정보 저장
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        // 토큰이 유효하지 않거나 없는 경우, 그냥 다음 필터로 진행 (인증되지 않은 상태)
        log.debug("JWT Token is invalid or not present for path: {}", request.getURI().getPath());
        return chain.filter(exchange);
    }
}