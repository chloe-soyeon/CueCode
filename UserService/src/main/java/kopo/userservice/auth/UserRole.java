package kopo.userservice.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 시스템 사용자 역할 (보호자, 환자)
 */
@AllArgsConstructor
@Getter
public enum UserRole {

    MANAGER("ROLE_MANAGER"), // 보호자
    PATIENT("ROLE_PATIENT"); // 환자

    private final String value;
}
