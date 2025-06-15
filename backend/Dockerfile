FROM maven:3.9.8-eclipse-temurin-21 AS dependencies

WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn/

RUN mvn dependency:go-offline -B


FROM dependencies AS build

COPY src/ ./src/

RUN mvn clean package -DskipTests -B

FROM dependencies AS test

COPY src/ ./src/

ENV SPRING_PROFILES_ACTIVE=test
ENV MAVEN_OPTS="-Xmx1024m -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

EXPOSE 8080

CMD ["mvn", "test"]

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001

COPY --from=build /app/target/*.jar app.jar

RUN chown spring:spring app.jar

USER spring:spring

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]