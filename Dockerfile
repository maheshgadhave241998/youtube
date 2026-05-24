# =========================
# STAGE 1: BUILD SPRING BOOT APP
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# =========================
# STAGE 2: RUNTIME
# =========================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install required system dependencies
# Install required system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ffmpeg \
    curl \
    unzip \
    python3 \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Install latest yt-dlp
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp \
    -o /usr/local/bin/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

# Install Deno
RUN curl -fsSL https://deno.land/install.sh | sh

# Add Deno to PATH
ENV DENO_INSTALL="/root/.deno"
ENV PATH="$DENO_INSTALL/bin:$PATH"

# Verify installations
RUN yt-dlp --version
RUN ffmpeg -version
RUN deno --version

# Copy Spring Boot JAR
COPY --from=build /app/target/*.jar app.jar

# OPTIONAL: if you use cookies file
COPY cookies.txt /app/cookies.txt

# Expose app port
EXPOSE 8080

# Run application
ENTRYPOINT ["java","-jar","app.jar"]