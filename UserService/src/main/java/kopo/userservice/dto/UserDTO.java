package kopo.userservice.dto;

public interface UserDTO {
    String id();        // 로그인 ID(=PK)
    String pw();        // 해시된 비밀번호
    String email();     // 이메일 (Patient는 null 가능)
    String name();      // 이름
    Role role();        // MANAGER or PATIENT

    // 스프링 시큐리티 권한 문자열
    default String authority() {
        return "ROLE_" + role().name();
    }

    enum Role { MANAGER, PATIENT }
}
