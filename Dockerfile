# 빌드 단계
FROM gradle:7.4.2-jdk17-alpine as builder
WORKDIR /build

# Gradle 파일 복사 및 의존성 설치
COPY build.gradle settings.gradle /build/
RUN gradle wrapper --gradle-version 7.4.2
RUN ./gradlew build -x test --parallel --continue > /dev/null 2>&1 || true

# 소스 코드 복사 및 애플리케이션 빌드
COPY . /build
RUN ./gradlew build -x test --parallel

# 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌더 이미지에서 jar 파일만 복사
COPY --from=builder /build/build/libs/midpoint-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# 비루트 권한으로 애플리케이션 실행
USER nobody
ENTRYPOINT [ \
   "java", \
   "-jar", \
   "-Djava.security.egd=file:/dev/./urandom", \
   "-Dsun.net.inetaddr.ttl=0", \
   "/app.jar" \
]