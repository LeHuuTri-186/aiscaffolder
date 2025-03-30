# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM maven:3.9.6-eclipse-temurin-21-jammy
WORKDIR /app

# Cài đặt zip và tạo user maven
RUN apt-get update && \
    apt-get install -y zip && \
    useradd -m -u 1000 maven && \
    mkdir -p /app/output && \
    mkdir -p /app/lib && \
    chown -R maven:maven /app

# Copy jar file vào thư mục lib
COPY --from=build /app/target/*.jar /app/lib/app.jar
RUN chown maven:maven /app/lib/app.jar

# Chuyển sang user maven
USER maven

# Các biến môi trường sẽ được truyền từ docker-compose.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/lib/app.jar"] 