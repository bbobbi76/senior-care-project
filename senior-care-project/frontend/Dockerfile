#Node로 빌드하고 Nginx로 서빙합니다.

# 1단계: 빌드
FROM node:18-alpine AS builder
WORKDIR /build
COPY package.json package-lock.json* ./
# package-lock.json이 없을 수도 있으므로 *를 붙임
RUN npm install
COPY . .
RUN npm run build

# 2단계: 서빙
FROM nginx:1.27-alpine
COPY --from=builder /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]