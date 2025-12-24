# ---- build stage ----
FROM gradle:8.10-jdk17 AS build
WORKDIR /home/gradle/project
COPY . .
RUN gradle :payments-service:bootJar --no-daemon

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/project/payments-service/build/libs/app.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
