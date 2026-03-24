FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN groupadd --system rzd && useradd --system -g rzd rzd

USER rzd

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}", "/app/app.jar"]