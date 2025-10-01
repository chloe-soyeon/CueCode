package kopo.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "patients")
public class Patient {
    @Id
    private String id;
    private List<String> managerids;
    private String email;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getManagerids() { return managerids; }
    public void setManagerids(List<String> managerids) { this.managerids = managerids; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

