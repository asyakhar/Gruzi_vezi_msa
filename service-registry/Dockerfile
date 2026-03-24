FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/*.jar registry.jar

RUN groupadd --system rzd && useradd --system -g rzd rzd

USER rzd

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}", "/app/registry.jar"]