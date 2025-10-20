# 1. Pilih base image yang sesuai, gunakan openjdk untuk menjalankan aplikasi Java
FROM openjdk:17-jdk-slim

# 2. Set working directory dalam container
WORKDIR /app

# 3. Salin file JAR aplikasi ke dalam container
COPY target/myapp.jar /app/myapp.jar

# 4. Expose port yang digunakan oleh aplikasi (biasanya 8080)
EXPOSE 8080

# 5. Tentukan perintah untuk menjalankan aplikasi Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]