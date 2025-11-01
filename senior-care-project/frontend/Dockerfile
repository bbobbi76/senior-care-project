# 1단계: 빌드 환경 (Node.js 18 사용)
FROM node:18-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /build

# 1. 의존성 파일만 먼저 복사하여 NPM 설치 (캐시 활용)
COPY package.json package-lock.json* ./
RUN npm install

# 2. 소스 코드 및 설정 파일 복사
# (수정!) vite.config.js 파일을 복사하는 라인을 추가합니다.
COPY src ./src
COPY index.html .
COPY tailwind.config.js .
COPY postcss.config.js .
COPY vite.config.js .  

# Vue 빌드 실행
RUN npm run build

# 2단계: 서빙 환경 (Nginx 웹서버 사용)
FROM nginx:1.27-alpine

# Nginx의 기본 루트 폴더에 'dist' 폴더의 내용물을 복사
COPY --from=builder /build/dist /usr/share/nginx/html

# Nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# 컨테이너가 80 포트를 노출
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
