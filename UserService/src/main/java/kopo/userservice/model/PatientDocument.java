package kopo.userservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "patients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDocument {
    @Id
    private String id;
    private String name;
    private String email;
    private String pw;
    private List<String> managerIds;
    private Integer age;
    private String gender;
    private String room;
    private String bed;
    private String doctorName;
    private String admissionDate;
    @Builder.Default
    private String medicalHistory = "";
    @Builder.Default
    private List<String> medications = new ArrayList<>();
    @Builder.Default
    private List<String> allergies = new ArrayList<>();
}

