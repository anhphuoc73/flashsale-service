# ========= STAGE 1: BUILD =========
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ========= STAGE 2: RUN =========
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/flashsale-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]