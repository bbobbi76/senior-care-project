package com.example.seniorcarebackend;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException; 
import java.io.File; // File 클래스 임포트

@SpringBootApplication
public class SeniorCareBackendApplication {

    @Value("${SPRING_CLOUD_GCP_PROJECT_ID}") 
    private String gcpProjectId;
    
    public static void main(String[] args) {
        SpringApplication.run(SpringApplication.class, args);
    }

    /**
     * Firestore 클라이언트 객체를 Spring Bean으로 등록
     */
    @Bean
    public Firestore firestore(
        @Value("${SPRING_CLOUD_GCP_CREDENTIALS_LOCATION}") String serviceAccountKeyPath 
    ) throws IOException {
        
        // File 객체를 바로 사용합니다.
        File keyFile = new File(serviceAccountKeyPath);

        if (!keyFile.exists()) {
             System.err.println("FATAL ERROR: GCP KEY FILE NOT FOUND at: " + serviceAccountKeyPath);
             throw new FileNotFoundException("GCP Service Account key file not found at " + serviceAccountKeyPath + 
                                            ". Check your docker-compose.yml 'volumes' and .env file.");
        }

        FileInputStream serviceAccount = new FileInputStream(keyFile); // <-- File 객체를 바로 사용

        // 데이터베이스 URL에 리전과 프로젝트 ID를 명시적으로 지정하여 NOT_FOUND 오류 회피
        // GCP 서울 리전 (asia-northeast3)
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId(gcpProjectId)
                .setDatabaseUrl("https://" + gcpProjectId + ".asia-northeast3.firebaseio.com") 
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
    
    @Bean
    public EventStorageService eventStorageService(Firestore firestore) {
        return new EventStorageService(firestore);
    }
}