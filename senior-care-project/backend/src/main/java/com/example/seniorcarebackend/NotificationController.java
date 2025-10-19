// 간단한 '트리거' API

package com.example.seniorcarebackend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") // Nginx가 /api/로 전달
public class NotificationController {

    private final EventStorageService eventStorageService;

    /**
     * (시뮬레이션) Vue에서 영상의 특정 시점에 이 API를 호출
     */
    @PostMapping("/trigger/fall-detection")
    public ResponseEntity<String> triggerFallDetection() {
        try {
            eventStorageService.saveFallDetectionEvent();
            return ResponseEntity.ok("이벤트 저장 성공");
        } catch (Exception e) {
            System.err.println("Firestore 저장 실패: " + e.getMessage());
            return ResponseEntity.status(500).body("DB 저장 실패");
        }
    }

    /**
     * 모든 이벤트 기록을 조회
     */
    @GetMapping("/events")
    public ResponseEntity<List<DetectionEvent>> getAllEvents() {
        try {
            List<DetectionEvent> events = eventStorageService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}