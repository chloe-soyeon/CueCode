package kopo.userservice.repository;

import kopo.userservice.repository.document.ManagerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends MongoRepository<ManagerDocument, String> {
    Optional<ManagerDocument> findById(String id);
}
