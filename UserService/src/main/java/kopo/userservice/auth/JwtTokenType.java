package kopo.userservice.auth;

public enum JwtTokenType {
    ACCESS_TOKEN,   // 요청 인증에 쓰이는 짧은 만료 토큰
    REFRESH_TOKEN   // Access 토큰 재발급용 긴 만료 토큰
}
