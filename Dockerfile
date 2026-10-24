# Multi-stage production image for Railway.
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
COPY ../../../Downloads/dara-massalik-changements/backend/src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN apk add --no-cache su-exec     && addgroup -S spring     && adduser -S spring -G spring     && mkdir -p /app/uploads/student-photos     && chown -R spring:spring /app
COPY --from=build /workspace/target/dara-massalik-backend-0.1.0.jar /app/app.jar
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 755 /usr/local/bin/docker-entrypoint.sh
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV UPLOADS_DIR=/app/uploads/student-photos
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
