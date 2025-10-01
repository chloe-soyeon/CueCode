package kopo.motionservice.controller;

import kopo.motionservice.dto.MotionRecordRequestDTO;
import kopo.motionservice.service.IMotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/motions")
@RequiredArgsConstructor
public class MotionController {

    private final IMotionService motionService;

    @PostMapping("/register")
    public ResponseEntity<String> registerMotion(@RequestBody MotionRecordRequestDTO requestDTO) {
        log.info("[MotionController] Received motion registration request for phrase: {}", requestDTO.getPhrase());

        // [DEBUG] DTO의 motionData가 null인지 확인
        if (requestDTO.getMotionData() == null) {
            log.error("[DEBUG-Controller] motionData is NULL after JSON parsing!");
        } else {
            log.info("[DEBUG-Controller] motionData is NOT null. face_blendshapes count: {}", 
                requestDTO.getMotionData().getFaceBlendshapes() != null ? requestDTO.getMotionData().getFaceBlendshapes().size() : "null");
        }

        motionService.saveRecordedMotion(requestDTO);

        return ResponseEntity.ok("Motion registered successfully.");
    }
}
