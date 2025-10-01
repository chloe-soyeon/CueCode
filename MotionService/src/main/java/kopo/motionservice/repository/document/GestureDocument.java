package kopo.motionservice.repository.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "gestures")
public class GestureDocument {

    @Id
    private String gestureId;

    // 요청하신 환자 ID 필드 추가
    private String patientId;

    // 기존 AdminGestureList의 필드들
    private String gestureLabel; // 예: DOUBLE_BLINK, NOD
    private String gestureName;  // 예: 눈 두번 깜빡이기, 고개 끄덕임
    private String description;  // 제스처에 대한 간단한 설명

}
