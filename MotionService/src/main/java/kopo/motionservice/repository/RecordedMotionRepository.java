package kopo.motionservice.repository;

import kopo.motionservice.repository.document.RecordedMotionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordedMotionRepository extends MongoRepository<RecordedMotionDocument, String> {
}
