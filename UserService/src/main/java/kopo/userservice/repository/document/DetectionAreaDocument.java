package kopo.userservice.repository.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "detection_area")
public class DetectionAreaDocument {
    private String patientId;
    private boolean hand;
    private boolean face;
    private boolean both;
}

