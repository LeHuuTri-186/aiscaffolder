# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Prevent copying unnecessary files
COPY . .
RUN mvn -Dmaven.repo.local=/app/.m2 clean package -DskipTests

# Run stage
FROM maven:3.9.6-eclipse-temurin-21-jammy
WORKDIR /app

# Install required tools
RUN apt-get update && \
    apt-get install -y zip curl unzip && \
    curl -s "https://get.sdkman.io" | bash && \
    bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install gradle 8.5" && \
    mv /root/.sdkman /opt/sdkman && \
    useradd -m -u 1000 maven && \
    mkdir -p /app/output /app/lib && \
    chown -R maven:maven /app /opt/sdkman

# Set environment variables
ENV SDKMAN_DIR="/opt/sdkman"
ENV PATH="$SDKMAN_DIR/candidates/gradle/current/bin:$PATH"

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar /app/lib/app.jar
RUN chown maven:maven /app/lib/app.jar

# Switch to maven user
USER maven

# Expose port and start application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/lib/app.jar"]
