FROM eclipse-temurin:17.0.8_7-jdk-focal

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean build

ARG JAR_FILE="build/libs/*.jar"
COPY ${JAR_FILE} app.jar

ARG PROFILE="local"
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

ENTRYPOINT ["java", "-jar", "/app.jar"]