#Build
FROM --platform=$BUILDPLATFORM maven:3.9.1-eclipse-temurin-17-alpine AS build
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean compile assembly:single

#Package
FROM --platform=$BUILDPLATFORM eclipse-temurin:17-alpine AS package
COPY --from=build /usr/src/app/target/bot-checker-*.jar /usr/app/bot-checker.jar
ENTRYPOINT ["java", "-jar", "/usr/app/bot-checker.jar"]
