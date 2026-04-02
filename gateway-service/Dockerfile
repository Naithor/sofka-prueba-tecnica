FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY shared-lib/pom.xml shared-lib/
COPY gateway-service/pom.xml gateway-service/
COPY gateway-service/pom.xml gateway-service/
RUN mvn dependency:go-offline -B
COPY . .
RUN mvn package -DskipTests -pl gateway-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/gateway-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
