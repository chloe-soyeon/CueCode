package kopo.motionservice.service.impl;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import kopo.motionservice.dto.MotionRecordRequestDTO;
import kopo.motionservice.repository.RecordedMotionRepository;
import kopo.motionservice.repository.document.RecordedMotionDocument;
import kopo.motionservice.service.IMotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
    public String sendMotionVideoToFastAPI(
            String phrase,
            String detectionArea,
            org.springframework.web.multipart.MultipartFile videoFile,
            String trimStart,
            String trimEnd
    ) {
        log.info("[MotionService] Streaming FFmpeg → FastAPI (no output file): phrase={}, area={}, ss={}, to={}",
                phrase, detectionArea, trimStart, trimEnd);

        // Build a pipe: FFmpeg writes to ffOut, HTTP reads from ffIn
        final int PIPE_BUF = 1 << 20; // 1MB pipe buffer helps smooth backpressure
        try (PipedOutputStream ffOut = new PipedOutputStream();
             PipedInputStream ffIn  = new PipedInputStream(ffOut, PIPE_BUF);
             CloseableHttpClient httpClient = HttpClients.custom().build()) {

            // ---- 1) Kick off FFmpeg in a background thread
            var es = java.util.concurrent.Executors.newSingleThreadExecutor();
            var ffTask = es.submit(() -> {
                try (var inStream = videoFile.getInputStream()) {
                    // Use the uploaded stream as FFmpeg input (no temp file)
                    var input = com.github.kokorin.jaffree.ffmpeg.PipeInput.pumpFrom(inStream)
                            // If input is WebM (typical from MediaRecorder), set format:
                            .setFormat("webm");

                    var out = com.github.kokorin.jaffree.ffmpeg.PipeOutput.pumpTo(ffOut)
                            .setCodec(StreamType.VIDEO, "copy")
                            .setCodec(StreamType.AUDIO, "libopus") // re-encode audio for compatibility
                            .addArgument("-f").addArgument("webm");

                    var ff = FFmpeg.atPath(); // if ffmpeg isn't on PATH: FFmpeg.atPath(Paths.get("C:\\ffmpeg\\bin"))

                    ff.addInput(input);

                    if (trimStart != null && !trimStart.isBlank()) {
                        ff.addArgument("-ss").addArgument(trimStart);
                    }
                    if (trimEnd != null && !trimEnd.isBlank()) {
                        ff.addArgument("-to").addArgument(trimEnd);
                    }

                    ff.addOutput(out).execute();
                } catch (Exception e) {
                    log.error("[MotionService] FFmpeg streaming failed", e);
                    // If FFmpeg fails, make sure to close the pipe so the HTTP layer sees EOF
                } finally {
                    try { ffOut.close(); } catch (Exception ignore) {}
                }
            });

            // ---- 2) Build a streaming multipart request to FastAPI
            var filePart = new InputStreamResource(ffIn) {
                @Override public String getFilename() { return "motion.webm"; } // give it a name
                @Override public long contentLength() { return -1; }           // unknown → chunked
            };

            var body = new LinkedMultiValueMap<String, Object>();
            body.add("phrase", phrase);
            body.add("detectionArea", detectionArea);
            body.add("videoFile", filePart);

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            var requestFactory = new org.springframework.http.client.HttpComponentsClientHttpRequestFactory(httpClient);
            requestFactory.setConnectTimeout(10_000);
            requestFactory.setReadTimeout(120_000);

            // Important: with InputStreamResource + unknown length, HC5 will stream chunked
            var rest = new org.springframework.web.client.RestTemplate(requestFactory);
            var req  = new HttpEntity<>(body, headers);

            ResponseEntity<String> resp = rest.exchange(
                    "http://localhost:8000/api/process-motion",
                    HttpMethod.POST,
                    req,
                    String.class
            );

            // Wait for ffmpeg thread to finish (or time out)
            try { ffTask.get(2, java.util.concurrent.TimeUnit.MINUTES); }
            catch (Exception e) { ffTask.cancel(true); }

            es.shutdownNow();

            log.info("[MotionService] FastAPI status={}, bodyLen={}",
                    resp.getStatusCode(), (resp.getBody() == null ? 0 : resp.getBody().length()));

            return resp.getBody();

        } catch (IOException e) {
            log.error("[MotionService] I/O error during streaming", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String sendMotionVideoToFastAPI(String phrase, String detectionArea, org.springframework.web.multipart.MultipartFile videoFile) {
        // Delegate to the main method with null trimStart/trimEnd
        return sendMotionVideoToFastAPI(phrase, detectionArea, videoFile, null, null);
    }
}
