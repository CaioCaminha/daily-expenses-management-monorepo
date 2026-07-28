FROM bellsoft/liberica-openjdk-alpine:21.0.1 as builder

ARG JAR_FILE=build/libs/java-daily-expenses-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

run java -Djarmode=layertools -jar app.jar extract

RUN addgroup -g 10001 -S spring && \
    adduser -u 10001 -G spring -S spring

USER spring:spring

FROM bellsoft/liberica-runtime-container:jre-21-glibc
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

