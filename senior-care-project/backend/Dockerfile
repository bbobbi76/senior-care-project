# Python 3.10-alpine (가벼운) 환경 사용
FROM python:3.10-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 1. 의존성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 2. 소스 코드 복사
COPY server.py .

# 컨테이너가 8080 포트를 노출
EXPOSE 8080

# Flask 서버 실행 명령어
CMD ["python", "server.py"]