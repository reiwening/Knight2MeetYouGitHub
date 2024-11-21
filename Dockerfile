# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /workspace

# Copy the built artifact from build stage
COPY --from=build /workspace/target/*.jar app.jar

# Create data directory and set permissions
RUN mkdir -p /workspace/data && \
    addgroup -S spring && \
    adduser -S spring -G spring && \
    chown -R spring:spring /workspace

USER spring:spring

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Create an entrypoint script
USER root
RUN echo '#!/bin/sh' > /workspace/entrypoint.sh && \
    echo 'if [ -z "$OPENAI_API_KEY" ]; then' >> /workspace/entrypoint.sh && \
    echo '  echo "Error: OPENAI_API_KEY environment variable is not set"' >> /workspace/entrypoint.sh && \
    echo '  exit 1' >> /workspace/entrypoint.sh && \
    echo 'fi' >> /workspace/entrypoint.sh && \
    echo 'exec java -jar app.jar' >> /workspace/entrypoint.sh && \
    chmod +x /workspace/entrypoint.sh && \
    chown spring:spring /workspace/entrypoint.sh

USER spring:spring

# Expose the application port
EXPOSE 8080

# Run the application using the entrypoint script
ENTRYPOINT ["/workspace/entrypoint.sh"]
