package kopo.userservice.repository;

import kopo.userservice.repository.document.PatientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<PatientDocument, String> {
    Optional<PatientDocument> findById(String id);
}
