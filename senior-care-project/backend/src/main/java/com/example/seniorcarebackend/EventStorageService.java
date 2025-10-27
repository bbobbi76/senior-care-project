package com.example.seniorcarebackend;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Component; 

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component //  Spring이 자동으로 Bean으로 등록
public class EventStorageService {

    private final Firestore firestore;
    private final CollectionReference eventsCollection;

    // 생성자를 통해 Firestore 객체를 주입, 컬렉션을 정의
    public EventStorageService(Firestore firestore) {
        this.firestore = firestore;
        this.eventsCollection = firestore.collection("detection_events");
    }

    public DetectionEvent saveFallDetectionEvent() {
        DetectionEvent event = new DetectionEvent(null, "FALL_DETECTED", Instant.now());
        eventsCollection.add(event);
        System.out.println("Firestore 저장 완료: " + event);
        return event;
    }

    public List<DetectionEvent> getAllEvents() {
        try {
            Query query = eventsCollection.orderBy("timestamp", Query.Direction.DESCENDING);
            QuerySnapshot querySnapshot = query.get().get();

            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(DetectionEvent.class))
                    .collect(Collectors.toList());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Firestore 조회 중 오류 발생: " + e.getMessage());
            return List.of(); 
        }
    }
}
