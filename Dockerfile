# syntax=docker/dockerfile:1
FROM eclipse-temurin:11-jre

WORKDIR /app

# Copy the Spring Boot fat jar built by Maven (classifier: exec)
COPY goodface-user-app/target/*exec.jar /app/app.jar

VOLUME ["/app/logs"]
EXPOSE 8002 20882 22222

ENV JAVA_TOOL_OPTIONS="" \
    TZ=Asia/Shanghai

ENTRYPOINT ["java","-jar","/app/app.jar"]