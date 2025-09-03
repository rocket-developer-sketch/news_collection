# 빌드
FROM gradle:8.7-jdk17 AS builder
ARG MODULE=api
WORKDIR /home/gradle/project

# Gradle의 프로젝트 루트 설정과 래퍼 구성 복사
COPY settings.gradle .
COPY build.gradle .
COPY gradle gradle

# 빌드 대상 모듈 개별 설정 복사
COPY ${MODULE}/build.gradle ${MODULE}/build.gradle

# 참조 대상 모듈 설정 복사
COPY domain/build.gradle domain/build.gradle

# 빌드 전 의존성 캐시
RUN gradle :${MODULE}:dependencies

# 의존성 변경 되면 프로젝트 루트 설정 등 복사 다시 시작(캐시 무효화)
#settings.gradle(.kts)
#build.gradle(.kts)
#gradle/wrapper/gradle-wrapper.properties
#모듈 간 의존 연결 (project(":domain") 등)

COPY ${MODULE}/src ${MODULE}/src
COPY domain/src domain/src

RUN gradle clean  :${MODULE}:bootJar --no-daemon


FROM openjdk:17-jdk-slim
ARG MODULE=api
WORKDIR /app
COPY --from=builder /home/gradle/project/${MODULE}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]