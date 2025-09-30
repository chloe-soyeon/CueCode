package kopo.motionservice.repository;

import kopo.motionservice.repository.document.GestureDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GestureRepository extends MongoRepository<GestureDocument, String> {
}
