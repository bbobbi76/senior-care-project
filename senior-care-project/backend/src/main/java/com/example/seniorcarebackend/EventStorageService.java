// 이벤트 저장/조회 로직

package com.example.seniorcarebackend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventStorageService {

    private final DetectionEventRepository eventRepository;

    // "FALL_DETECTED" 이벤트를 Firestore에 저장
    public DetectionEvent saveFallDetectionEvent() {
        DetectionEvent event = new DetectionEvent(null, "FALL_DETECTED", Instant.now());
        System.out.println("Firestore 저장 시도: " + event);
        return eventRepository.save(event);
    }

    // 모든 이벤트를 최신순으로 조회
    public List<DetectionEvent> getAllEvents() {
        return eventRepository.findAllByOrderByTimestampDesc();
    }
}