# ---- build stage ----
FROM gradle:8.10-jdk17 AS build
WORKDIR /home/gradle/project
COPY . .
RUN gradle :orders-service:bootJar --no-daemon

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/project/orders-service/build/libs/app.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
