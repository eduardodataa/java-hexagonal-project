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

## Testing SQS Messages

### PowerShell (Windows)
```powershell
# Test all commands
.\test-sqs.ps1 all

# Test specific commands
.\test-sqs.ps1 create
.\test-sqs.ps1 update
.\test-sqs.ps1 cancel
.\test-sqs.ps1 events
```

### Bash (Linux/Mac)
```bash
# Test all commands
./test-sqs.sh all

# Test specific commands
./test-sqs.sh create
./test-sqs.sh update
./test-sqs.sh cancel
./test-sqs.sh events
```

## Architecture Notes

- **No REST API**: System communicates exclusively via SQS
- **Two Queues**: `order-commands` (input) and `order-events` (output)
- **Event-Driven**: All operations triggered by SQS messages
- **Hexagonal Architecture**: Clean separation of domain and infrastructure
