# ---- Build stage ----
FROM gradle:8.14-jdk21 AS build
WORKDIR /app

# Cache dependencies separately from source for faster rebuilds
COPY build.gradle.kts settings.gradle.kts* gradle.properties* ./
RUN gradle dependencies --no-daemon || true

COPY src ./src
RUN gradle shadowJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/build/libs/*-all.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]