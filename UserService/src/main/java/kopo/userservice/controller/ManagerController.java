package kopo.userservice.controller;

import kopo.userservice.service.PatientManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private PatientManagerService patientManagerService;

    @PostMapping("/addPatient")
    public ResponseEntity<?> addPatient(@RequestBody Map<String, String> req, Principal principal) {
        String patientId = req.get("patientId");
        String managerId = principal.getName(); // 인증 정보에서 보호자 ID 추출 (JWT 인증 시)
        boolean result = patientManagerService.addPatientToManager(managerId, patientId);
        if (result) {
            return ResponseEntity.ok(Map.of("result", 200, "msg", "환자 추가 성공"));
        } else {
            return ResponseEntity.status(400).body(Map.of("result", 400, "msg", "환자 또는 보호자 정보 없음"));
        }
    }
}

