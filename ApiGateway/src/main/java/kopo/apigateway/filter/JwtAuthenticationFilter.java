package kopo.apigateway.filter;

import kopo.apigateway.dto.TokenDTO;
import kopo.apigateway.jwt.JwtStatus;
import kopo.apigateway.jwt.JwtTokenProvider;
import kopo.apigateway.jwt.JwtTokenType;
import kopo.apigateway.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${jwt.token.access.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.token.access.name}")
    private String accessTokenName;

    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static final String[] PERMIT_ALL_PATHS = {
            "/login/**",
            "/reg/**",             // 백엔드 직접 호출 대비
            "/user/reg/**",        // 게이트웨이 경유 회원가입
            "/user/actuator/**",   // 게이트웨이 경유 액추에이터
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private ResponseCookie deleteTokenCookie(String tokenName) {
        log.info("deleteTokenCookie: tokenName={}", tokenName);
        return ResponseCookie.from(tokenName, "")
                .maxAge(0)
                .build();
    }

    private ResponseCookie createTokenCookie(String tokenName, long tokenValidTime, String token) {
        log.info("createTokenCookie: tokenName={}, tokenValidTime={}, token={}", tokenName, tokenValidTime, token);
        return ResponseCookie.from(tokenName, token)
                .path("/")
                .maxAge(tokenValidTime)
                .httpOnly(true)
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getPath().value();
        log.info("[JWT 필터] 요청 경로: {}", path);

        // 인증이 필요 없는 경로는 필터 통과
        for (String permitAllPath : PERMIT_ALL_PATHS) {
            if (antPathMatcher.match(permitAllPath, path)) {
                log.info("[JWT 필터] 인증 제외 경로: {}", path);
                return chain.filter(exchange);
            }
        }

        // Access Token 추출 및 검증
        String accessToken = CmmUtil.nvl(jwtTokenProvider.resolveToken(request, JwtTokenType.ACCESS_TOKEN));
        log.info("[JWT 필터] AccessToken: {}", accessToken);
        JwtStatus accessTokenStatus = jwtTokenProvider.validateToken(accessToken);
        log.info("[JWT 필터] AccessToken 상태: {}", accessTokenStatus);

        if (accessTokenStatus == JwtStatus.ACCESS) {
            TokenDTO accessInfo = jwtTokenProvider.getTokenInfo(accessToken);
            log.info("[JWT 필터] AccessToken userId: {}", accessInfo.userId());
            log.info("[JWT 필터] AccessToken userRoles: {}", accessInfo.role());
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            log.info("[JWT 필터] 인증 성공, SecurityContext에 저장");
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        // Access Token 만료 또는 거부 시 Refresh Token 검증
        if (accessTokenStatus == JwtStatus.EXPIRED || accessTokenStatus == JwtStatus.DENIED) {
            String refreshToken = CmmUtil.nvl(jwtTokenProvider.resolveToken(request, JwtTokenType.REFRESH_TOKEN));
            log.info("[JWT 필터] RefreshToken: {}", refreshToken);
            JwtStatus refreshTokenStatus = jwtTokenProvider.validateToken(refreshToken);
            log.info("[JWT 필터] RefreshToken 상태: {}", refreshTokenStatus);

            if (refreshTokenStatus == JwtStatus.ACCESS) {
                TokenDTO refreshInfo = jwtTokenProvider.getTokenInfo(refreshToken);
                log.info("[JWT 필터] RefreshToken userId: {}", refreshInfo.userId());
                log.info("[JWT 필터] RefreshToken userRoles: {}", refreshInfo.role());
                String reAccessToken = jwtTokenProvider.createToken(refreshInfo.userId(), refreshInfo.role());
                log.info("[JWT 필터] AccessToken 재발급: {}", reAccessToken);
                response.addCookie(deleteTokenCookie(accessTokenName));
                response.addCookie(createTokenCookie(accessTokenName, accessTokenValidTime, reAccessToken));
                Authentication authentication = jwtTokenProvider.getAuthentication(reAccessToken);
                log.info("[JWT 필터] 재발급된 AccessToken 인증 성공, SecurityContext에 저장");
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } else {
                log.info("[JWT 필터] RefreshToken도 만료 또는 거부됨, 인증 불가");
            }
        }
        log.info("[JWT 필터] 인증 실패 또는 토큰 없음, 필터 종료");
        return chain.filter(exchange);
    }
}
