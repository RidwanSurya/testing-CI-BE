## Stage 1: Build aplikasi Spring Boot menggunakan Gradle
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# Salin semua file proyek ke container builder
COPY . .

# Build tanpa menjalankan test
RUN gradle clean build -x test

## Stage 2: Jalankan aplikasi dengan image OpenJDK ringan
FROM openjdk:17-jdk-slim
WORKDIR /app

# Salin file .jar hasil build dari stage sebelumnya
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port aplikasi Spring Boot (default: 8080)
EXPOSE 8081

# Environment variable untuk koneksi ke Oracle
ENV SPRING_DATASOURCE_URL=jdbc:oracle:thin:@oracle-db-app:1521/wandoor_db \
    SPRING_DATASOURCE_USERNAME=wandoor \
    SPRING_DATASOURCE_PASSWORD=root \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Jalankan aplikasi
CMD ["java", "-jar", "app.jar"]