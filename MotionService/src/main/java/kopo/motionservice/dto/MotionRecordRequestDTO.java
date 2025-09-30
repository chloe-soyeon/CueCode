package kopo.motionservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 프론트에서 보내는 JSON에 없는 필드는 무시
public class MotionRecordRequestDTO {

    private String label; // "안녕하세요" 등 동작에 부여할 이름
    private List<FrameDataDTO> frames; // 녹화된 프레임 데이터 묶음

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FrameDataDTO {
        private long timestamp_ms;
        private Map<String, Double> face_blendshapes;
        private List<List<Double>> left_hand_landmarks;
        private List<List<Double>> right_hand_landmarks;
    }
}
