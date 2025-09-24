package kopo.userservice.repository;

import kopo.userservice.repository.document.DetectionAreaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DetectionAreaRepository extends MongoRepository<DetectionAreaDocument, String> {
}

