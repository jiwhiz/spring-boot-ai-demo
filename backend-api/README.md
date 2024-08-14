# backend-api server
SpringBoot AI Chatbot backend API server for demo purpose.

## Local Development

We are using Java 21, and [TestContainers](https://www.testcontainers.org/)
to run local integration tests, etc.

### Docker
In order to setup local development environment, you first need to install
[Docker Desktop](https://docs.docker.com/desktop/) if you didn't have it in your computer.

Use Docker to setup local test environment with PostgreSQL. Run
```
docker compose up -d
```

### Java 21
Install Java 21 by [sdkman](https://sdkman.io/), and switch to JDK 21,
like `sdk use java 21.0.4-oracle`.

### Build with Gradle

```
./gradlew clean build
```

### Run with local PostgreSQL
```
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Then you can open Swagger UI at http://localhost:8080/swagger-ui/index.html

