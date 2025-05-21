# Usa immagine Java JDK
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copia il JAR dalla build di Gradle
COPY build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
