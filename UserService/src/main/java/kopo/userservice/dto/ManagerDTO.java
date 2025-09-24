package kopo.userservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ManagerDTO(
        String id,
        String pw,
        String email,          // NOT NULL
        String name,
        List<String> patientIds // 선택: 관리 환자 ID 리스트
) implements UserDTO {
    @Override public Role role() { return Role.MANAGER; }
}