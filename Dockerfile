FROM openjdk:17-jdk-alpine AS builder

WORKDIR /app

COPY . .

# Fix Windows line endings for BOTH the maven wrapper and properties files
RUN sed -i 's/\r$//' ./mvnw
RUN sed -i 's/\r$//' src/main/resources/application.properties  # <-- ADD THIS LINE

RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

# Note: I noticed your log shows eclipse-temurin.
# Make sure this matches the builder stage for consistency.
# Using openjdk from the builder stage here.
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]