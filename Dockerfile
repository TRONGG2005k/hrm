# Multi-stage build for Spring Boot application
# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# FIX permission
RUN chmod +x mvnw

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

ENV TZ=Asia/Ho_Chi_Minh

RUN apk add --no-cache tzdata

# Create non-root user
RUN addgroup -S hrmgroup && adduser -S hrmuser -G hrmgroup

WORKDIR /app

# Copy jar
COPY --from=builder /app/target/*.jar app.jar

# Upload folder
RUN mkdir -p ./uploads && chown -R hrmuser:hrmgroup ./uploads

USER hrmuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
 CMD wget --no-verbose --tries=1 --spider http://localhost:8081/api/v1/hello || exit 1

ENTRYPOINT ["java","-Duser.timezone=Asia/Ho_Chi_Minh","-jar","app.jar"]