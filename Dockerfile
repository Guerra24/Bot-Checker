#Build
FROM maven:3.9.1-eclipse-temurin-17-alpine AS build
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean compile assembly:single

#Package
FROM eclipse-temurin:17-alpine AS package
COPY --from=build /usr/src/app/target/bot-checker-1.1.0.jar /usr/app/bot-checker.jar