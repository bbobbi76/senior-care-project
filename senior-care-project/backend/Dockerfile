# 1단계: 빌드 환경 (Maven과 Java 17 사용)
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 작업 디렉토리 설정
WORKDIR /build

# 전체 프로젝트를 복사하여 한 번에 빌드
COPY . .

# Maven으로 프로젝트 빌드 (jar 파일 생성)
RUN mvn package -DskipTests

# 2단계: 실행 환경 (Java 17 JRE만 설치된 가벼운 이미지 사용)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
# 빌드 단계에서 생성된 jar 파일을 실행 단계로 복사
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
# 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]