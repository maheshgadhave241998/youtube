# Stage 1: Build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# Stage 2: Runtime image
FROM eclipse-temurin:17-jdk

WORKDIR /app

# ✅ IMPORTANT: clean + safe install (Render friendly)
FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ffmpeg \
    curl \
    yt-dlp \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]