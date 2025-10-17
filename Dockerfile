# syntax=docker/dockerfile:1

# ===== Build stage: use Maven with Temurin JDK 11 =====
FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /app

# Copy POMs first to leverage Docker layer cache for dependencies
COPY pom.xml ./
COPY goodface-user-api/pom.xml goodface-user-api/pom.xml
COPY goodface-user-repo/pom.xml goodface-user-repo/pom.xml
COPY goodface-user-service/pom.xml goodface-user-service/pom.xml
COPY goodface-user-app/pom.xml goodface-user-app/pom.xml
RUN mvn -B -ntp -DskipTests dependency:go-offline

# Copy the complete source tree and build only the app module (with dependencies)
COPY . .
RUN mvn -B -ntp -DskipTests -pl goodface-user-app -am package

# ===== Runtime stage: lightweight Temurin JRE 11 =====
FROM eclipse-temurin:11-jre AS runtime
WORKDIR /app

ENV JAVA_TOOL_OPTIONS="" \
    TZ=Asia/Shanghai

# Copy the Spring Boot executable jar built in the previous stage (classifier: exec)
COPY --from=build /app/goodface-user-app/target/*exec.jar /app/app.jar

VOLUME ["/app/logs"]
EXPOSE 8002 20882 22222

ENTRYPOINT ["java","-jar","/app/app.jar"]