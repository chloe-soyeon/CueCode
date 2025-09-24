package kopo.apigateway.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kopo.apigateway.dto.TokenDTO;
import kopo.apigateway.jwt.JwtStatus;
import kopo.apigateway.jwt.JwtTokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.creator}")
    private String creator;

    @Value("${jwt.token.access.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.token.access.name}")
    private String accessTokenName;

    @Value("${jwt.token.refresh.name}")
    private String refreshTokenName;

    public static final String HEADER_PREFIX = "Bearer ";

    private SecretKey secret;

    @PostConstruct
    protected void init() {
        secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // null 또는 빈 문자열을 안전하게 처리하는 유틸리티 메서드
    private String nvl(String str) {
        return (str == null) ? "" : str;
    }

    // JWT 토큰 생성
    public String createToken(String userId, String roles) {
        Claims claims = Jwts.claims()
                .setIssuer(creator)
                .setSubject(userId);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + (accessTokenValidTime * 1000)))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 정보 추출
    public TokenDTO getTokenInfo(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
        String userId = nvl(claims.getSubject());
        String role = nvl((String) claims.get("roles"));
        return TokenDTO.builder().userId(userId).role(role).build();
    }

    // 인증 객체 생성
    public Authentication getAuthentication(String token) {
        TokenDTO rDTO = getTokenInfo(token);
        String userId = nvl(rDTO.userId());
        String roles = nvl(rDTO.role());
        Set<GrantedAuthority> pSet = new HashSet<>();
        if (!roles.isEmpty()) {
            for (String role : roles.split(",")) {
                pSet.add(new SimpleGrantedAuthority(role));
            }
        }
        return new UsernamePasswordAuthenticationToken(userId, "", pSet);
    }

    // 쿠키/헤더에서 토큰 추출
    public String resolveToken(ServerHttpRequest request, JwtTokenType tokenType) {
        String token = "";
        String tokenName = "";
        if (tokenType == JwtTokenType.ACCESS_TOKEN) {
            tokenName = accessTokenName;
        } else if (tokenType == JwtTokenType.REFRESH_TOKEN) {
            tokenName = refreshTokenName;
        }
        HttpCookie cookie = request.getCookies().getFirst(tokenName);
        if (cookie != null) {
            token = nvl(cookie.getValue());
        }
        if (token.isEmpty()) {
            String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
                token = bearerToken.substring(7);
            }
        }
        return token;
    }

    // 토큰 상태 검증
    public JwtStatus validateToken(String token) {
        if (!token.isEmpty()) {
            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
                if (claims.getExpiration().before(new Date())) {
                    return JwtStatus.EXPIRED;
                } else {
                    return JwtStatus.ACCESS;
                }
            } catch (ExpiredJwtException e) {
                return JwtStatus.EXPIRED;
            } catch (JwtException | IllegalArgumentException e) {
                return JwtStatus.DENIED;
            }
        } else {
            return JwtStatus.DENIED;
        }
    }
}
