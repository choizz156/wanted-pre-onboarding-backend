FROM gradle:jdk17 AS build-stage

WORKDIR /app

COPY --chown=gradle:gradle . /app

RUN ./gradlew clean build

FROM eclipse-temurin:17.0.8_7-jdk-focal

WORKDIR /app

ARG JAR_FILE="build/libs/*.jar"
COPY --from=build-stage /app/$JAR_FILE app.jar

ARG PROFILE="local"
ENV SPRING_PROFILES_ACTIVE=$PROFILE

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]