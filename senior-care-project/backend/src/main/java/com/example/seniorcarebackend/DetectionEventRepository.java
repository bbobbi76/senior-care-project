// Firestore DB 조작기

package com.example.seniorcarebackend;

import com.google.cloud.spring.data.firestore.repository.FirestoreRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetectionEventRepository extends FirestoreRepository<DetectionEvent, String> {
    List<DetectionEvent> findAllByOrderByTimestampDesc();
}