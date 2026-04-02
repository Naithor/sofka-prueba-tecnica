# Dockerfile para Cuentas Service
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY shared-lib/pom.xml shared-lib/
COPY cuentas-service/pom.xml cuentas-service/

RUN mvn dependency:go-offline -B || true

COPY . .

RUN mvn package -DskipTests -pl cuentas-service -am

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/cuentas-service/target/cuentas-service-*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8082

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

