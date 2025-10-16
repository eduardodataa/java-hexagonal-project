# Gradle Help

## Available Tasks

### Build Tasks
- `build` - Builds the project
- `clean` - Cleans the build directory
- `jar` - Creates a JAR file

### Test Tasks
- `test` - Runs unit tests
- `jacocoTestReport` - Generates JaCoCo coverage report
- `pitest` - Runs PIT mutation testing

### Application Tasks
- `bootRun` - Runs the Spring Boot application
- `bootJar` - Creates an executable JAR

### Verification Tasks
- `check` - Runs all verification tasks
- `jacocoTestCoverageVerification` - Verifies test coverage

## Common Commands

```bash
# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# Run mutation testing
./gradlew pitest

# Build Docker image
docker build -t hexagonal-order-service .

# Run with Docker Compose
docker-compose up -d
```
