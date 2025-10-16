FROM --platform=$BUILDPLATFORM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle ./
COPY src/ src/

RUN ./gradlew clean build -x test

FROM --platform=$TARGETPLATFORM gcr.io/distroless/java21-debian12:latest

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "app.jar"]
