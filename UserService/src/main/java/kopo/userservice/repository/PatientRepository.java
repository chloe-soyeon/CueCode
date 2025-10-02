package kopo.userservice.repository;

import kopo.userservice.model.PatientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PatientRepository extends MongoRepository<PatientDocument, String> {
    // 커스텀 쿼리 필요시 여기에 작성
    List<PatientDocument> findByManagerIdsContaining(String managerId);
}
