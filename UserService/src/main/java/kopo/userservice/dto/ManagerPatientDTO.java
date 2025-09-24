package kopo.userservice.dto;

import lombok.Builder;

@Builder
public record ManagerPatientDTO(
        String managerId,   // FK → MANAGER.ID
        String patientId    // FK → PATIENT.ID
) {
}
