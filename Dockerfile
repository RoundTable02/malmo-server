# 멀티스테이지 빌드를 위한 빌드 스테이지
FROM openjdk:17-jdk-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 build.gradle 파일들을 먼저 복사 (의존성 캐싱을 위해)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle 의존성 다운로드 (캐시 최적화)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# 런타임 스테이지
FROM openjdk:17-jre-slim AS runtime

# 비루트 사용자 생성 (보안을 위해)
RUN groupadd -r malmo && useradd -r -g malmo malmo

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 파일의 소유권을 malmo 사용자로 변경
RUN chown malmo:malmo app.jar

# 비루트 사용자로 실행
USER malmo

# 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]