package kopo.userservice.repository.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "patients")
public class PatientDocument {
    private String id;
    private String pw;
    private String email;
    private String name;
    private List<String> managerIds;
}
