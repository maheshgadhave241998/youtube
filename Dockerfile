# Stage 1: Build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# Stage 2: Runtime image
FROM eclipse-temurin:17-jdk

WORKDIR /app

# ✅ Install required system dependencies
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    ffmpeg \
    curl \
    && pip3 install yt-dlp \
    && apt-get clean

# Copy jar
COPY --from=build /app/target/*.jar app.jar

# Render port
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]