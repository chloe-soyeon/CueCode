package kopo.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "managers")
public class Manager {
    @Id
    private String id;
    private List<String> patientids;
    private String email;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getPatientids() { return patientids; }
    public void setPatientids(List<String> patientids) { this.patientids = patientids; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

