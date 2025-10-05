package kopo.motionservice.service.impl;

import kopo.motionservice.dto.MotionRecordRequestDTO;
import kopo.motionservice.repository.RecordedMotionRepository;
import kopo.motionservice.repository.document.RecordedMotionDocument;
import kopo.motionservice.service.IMotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MotionServiceImpl implements IMotionService {

    private final RecordedMotionRepository recordedMotionRepository;

    @Override
    public void saveRecordedMotion(MotionRecordRequestDTO requestDTO) {
        log.info("[MotionService] Manual Mapping - Saving recorded motion for phrase: {}", requestDTO.getPhrase());

        // TODO: JWT 인증 구현 후, 토큰에서 실제 userId를 가져와야 합니다.
        String mockUserId = "user123";

        // DTO -> Document 수동 매핑 시작
        MotionRecordRequestDTO.MotionDataDTO motionDataDTO = requestDTO.getMotionData();
        RecordedMotionDocument.MotionDataDocument motionDataDocument = new RecordedMotionDocument.MotionDataDocument();

        if (motionDataDTO != null) {
            // face_blendshapes 수동 매핑
            List<RecordedMotionDocument.FaceBlendshapesFrameDocument> blendshapesFrames = Optional.ofNullable(motionDataDTO.getFaceBlendshapes()).orElse(new ArrayList<>()).stream()
                    .map(dtoFrame -> {
                        RecordedMotionDocument.FaceBlendshapesFrameDocument docFrame = new RecordedMotionDocument.FaceBlendshapesFrameDocument();
                        docFrame.setTimestampMs(dtoFrame.getTimestampMs());
                        docFrame.setValues(dtoFrame.getValues());
                        return docFrame;
                    }).collect(Collectors.toList());
            motionDataDocument.setFaceBlendshapes(blendshapesFrames);

            // hand_landmarks 수동 매핑
            List<RecordedMotionDocument.HandLandmarksFrameDocument> landmarksFrames = Optional.ofNullable(motionDataDTO.getHandLandmarks()).orElse(new ArrayList<>()).stream()
                    .map(dtoFrame -> {
                        RecordedMotionDocument.HandLandmarksFrameDocument docFrame = new RecordedMotionDocument.HandLandmarksFrameDocument();
                        docFrame.setTimestampMs(dtoFrame.getTimestampMs());
                        docFrame.setLeftHand(dtoFrame.getLeftHand());
                        docFrame.setRightHand(dtoFrame.getRightHand());
                        return docFrame;
                    }).collect(Collectors.toList());
            motionDataDocument.setHandLandmarks(landmarksFrames);
        }

        // 최종 Document 객체 생성
        RecordedMotionDocument document = RecordedMotionDocument.builder()
                .userId(mockUserId)
                .phrase(requestDTO.getPhrase())
                .motionType(requestDTO.getMotionType())
                .motionData(motionDataDocument)
                .build();

        // DB에 저장
        recordedMotionRepository.save(document);

        log.info("[MotionService] Motion saved successfully! recordId: {}", document.getRecordId());
    }

    @Override
    public String sendMotionVideoToFastAPI(String phrase, String detectionArea, org.springframework.web.multipart.MultipartFile videoFile) {
        log.info("[MotionService] Sending video to FastAPI for phrase: {}, detectionArea: {}", phrase, detectionArea);
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String fastApiUrl = "http://localhost:8000/api/process-motion"; // Update to your FastAPI endpoint

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

            org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            body.add("phrase", phrase);
            body.add("detectionArea", detectionArea);
            body.add("videoFile", new org.springframework.core.io.ByteArrayResource(videoFile.getBytes()) {
                @Override
                public String getFilename() {
                    return videoFile.getOriginalFilename();
                }
            });

            org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity =
                    new org.springframework.http.HttpEntity<>(body, headers);

            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, requestEntity, String.class);
            log.info("[MotionService] FastAPI response: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("[MotionService] Error sending video to FastAPI", e);
            return "Error: " + e.getMessage();
        }
    }
}
