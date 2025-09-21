# Build stage (optional if using prebuilt JAR)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml .
RUN ./mvnw -q -B -DskipTests=true dependency:go-offline
COPY src src
RUN ./mvnw -q -B -DskipTests=true package

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/rpg-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

