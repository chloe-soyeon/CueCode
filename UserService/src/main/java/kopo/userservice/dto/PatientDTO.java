package kopo.userservice.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record PatientDTO(
        String id,
        String pw,
        String email,           // NULL 허용
        String name,
        List<String> managerIds, // 선택: 연결 보호자 ID 리스트
        String detectionAreaType // hand/face/both 중 하나
) implements UserDTO {
    @Override public Role role() { return Role.PATIENT; }
}