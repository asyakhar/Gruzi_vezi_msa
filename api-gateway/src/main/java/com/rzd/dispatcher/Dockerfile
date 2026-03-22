FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar gateway.jar

RUN groupadd -r rzd && useradd -r -g rzd rzd
USER rzd

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:default}", "/app/gateway.jar"]