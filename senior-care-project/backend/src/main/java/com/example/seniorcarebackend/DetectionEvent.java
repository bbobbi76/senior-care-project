// Firestore 데이터 모델

package com.example.seniorcarebackend;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "detection_events")
public class DetectionEvent {
    @DocumentId private String id;
    private String eventType;
    private Instant timestamp;
}