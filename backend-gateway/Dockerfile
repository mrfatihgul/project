FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src ./src

RUN chmod +x mvnw \
    && ./mvnw -B -DskipTests package \
    && mv target/gateway-0.0.1-SNAPSHOT.jar /app/application.jar

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/application.jar ./application.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "application.jar"]
