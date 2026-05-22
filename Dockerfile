# =========================
# STAGE 1: BUILD SPRING BOOT APP
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# =========================
# STAGE 2: RUNTIME (RENDER)
# =========================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install required system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ffmpeg \
    curl \
    yt-dlp \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy Spring Boot JAR
COPY --from=build /app/target/*.jar app.jar

# OPTIONAL: if you use cookies file
# COPY cookies.txt /app/cookies.txt

# Render uses dynamic port
EXPOSE 8080

# Run application
ENTRYPOINT ["java","-jar","app.jar"]