ARG DOCKER_REGISTRY=
FROM ${DOCKER_REGISTRY}maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY settings.xml /root/.m2/settings.xml
COPY pom.xml ./

RUN mkdir -p src/main/java && \
    printf '%s\n' 'public class DockerCacheWarmup { public static void main(String[] args) {} }' > src/main/java/DockerCacheWarmup.java && \
    mvn package -Dmaven.test.skip=true -B && \
    rm -rf src target

COPY src ./src
RUN mvn package -Dmaven.test.skip=true -B

ARG DOCKER_REGISTRY=
FROM ${DOCKER_REGISTRY}eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9037

ENTRYPOINT ["java", "-jar", "app.jar"]
