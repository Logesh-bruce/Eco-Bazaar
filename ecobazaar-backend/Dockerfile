# Stage 1: Build
# Use a Maven image based on Eclipse Temurin (the modern replacement for OpenJDK)
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
# Use the Eclipse Temurin JRE (Runtime) image which is smaller and secure
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/EcoBazaar-module2-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","EcoBazaar-module2-0.0.1-SNAPSHOT.jar"]