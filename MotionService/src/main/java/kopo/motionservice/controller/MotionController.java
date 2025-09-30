package kopo.motionservice.controller;

import kopo.motionservice.dto.MotionRecordRequestDTO;
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

    // private final IMotionService motionService; // 서비스 계층은 다음 단계에서 추가

    @PostMapping("/register")
    public ResponseEntity<String> registerMotion(@RequestBody MotionRecordRequestDTO requestDTO) {
        log.info("[MotionController] Received motion registration request");

        // TODO: 다음 단계에서 서비스 계층을 호출하여 실제 저장 로직 수행
        // motionService.registerMotion(requestDTO);

        log.info("Label: {}", requestDTO.getLabel());
        log.info("Number of frames: {}", requestDTO.getFrames().size());

        return ResponseEntity.ok("Motion registration request received.");
    }
}
