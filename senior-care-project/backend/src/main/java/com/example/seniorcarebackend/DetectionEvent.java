package com.example.seniorcarebackend;

import com.google.firebase.database.annotations.NotNull; //  Firebase의 NotNull 사용
import com.google.cloud.firestore.annotation.DocumentId; //  Google Cloud 버전 유지
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Firestore에 저장될 때 컬렉션 이름 지정
public class DetectionEvent {

    @DocumentId // Firestore 문서의 ID가 됨
    private String id;

    @NotNull // 필드에 Null이 허용되지 않음을 표시
    private String eventType; 

    @NotNull
    private Instant timestamp; 
}
