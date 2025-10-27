# 1단계: 빌드 환경 (Node.js 18 사용)
FROM node:18-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /build

# 1. 의존성 파일만 먼저 복사하여 NPM 설치 (캐시 활용)
COPY package.json package-lock.json* ./
RUN npm install

# 2. 소스 코드 복사
COPY . .

# Vue 빌드 실행 (index.html, asset 등이 dist 폴더에 생성됨)
RUN npm run build

# 2단계: 서빙 환경 (Nginx 웹서버 사용)
FROM nginx:1.27-alpine

# (최종 해결!) Nginx 컨테이너로 빌드된 파일만 정확히 복사
# Nginx의 기본 루트 폴더에 'dist' 폴더의 내용물을 복사합니다.
# 이렇게 하면 Nginx가 index.html 파일을 찾을 수 있습니다.
COPY --from=builder /build/dist /usr/share/nginx/html

# Nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# 컨테이너가 80 포트를 노출
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]