package kopo.userservice.controller;

import kopo.userservice.dto.PatientDTO;
import kopo.userservice.model.PatientDocument;
import kopo.userservice.repository.PatientRepository;
import kopo.userservice.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/list")
    public List<PatientDocument> getPatientsByManager(@RequestParam String managerId) {
        return patientRepository.findByManagerIdsContaining(managerId);
    }

    @GetMapping("/detail")
    public PatientDTO getPatientDetail(@RequestParam String id) {
        log.info("[PatientController] /patient/detail?id={} called", id);
        Optional<PatientDocument> patientOptional = patientRepository.findById(id);
        if (patientOptional.isEmpty()) {
            return null;
        }

        PatientDocument patient = patientOptional.get();
        String decryptedEmail = "";
        try {
            if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
                decryptedEmail = EncryptUtil.decAES128CBC(patient.getEmail());
            }
        } catch (Exception e) {
            log.error("Failed to decrypt email for patient id: {}", id, e);
            decryptedEmail = "복호화 오류"; // 복호화 실패 시 표시할 메시지
        }

        return PatientDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .email(decryptedEmail)
                .managerIds(patient.getManagerIds())
                .medicalHistory(patient.getMedicalHistory())
                .medications(patient.getMedications())
                .allergies(patient.getAllergies())
                .build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePatient(@RequestBody PatientDocument req) {
        Optional<PatientDocument> optional = patientRepository.findById(req.getId());
        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body("환자를 찾을 수 없습니다.");
        }
        PatientDocument patient = optional.get();
        // if (req.getName() != null) patient.setName(req.getName()); // 이름은 수정 불가능
        if (req.getMedicalHistory() != null) patient.setMedicalHistory(req.getMedicalHistory());
        if (req.getMedications() != null) patient.setMedications(req.getMedications());
        if (req.getAllergies() != null) patient.setAllergies(req.getAllergies());
        // 필요시 다른 필드도 추가
        patientRepository.save(patient);
        return ResponseEntity.ok("수정 완료");
    }
}
