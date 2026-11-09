# Configuration Demo — Catalog Service

## Overview

This demonstration shows how Spring Boot supports externalized configuration. The same JAR file was used for all four tests without rebuilding the application.

The default `polar.greeting` property is defined in `application.yml`, but its value can be overridden using command-line arguments, JVM system properties, or environment variables.

## Test 1 — Command-Line Argument

Command:

```bash
java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar \
  --polar.greeting="Welcome to the catalog from CLI"
```

Response from `curl http://localhost:9001/`:

```text
Welcome to the catalog from CLI
```

Result: The command-line argument successfully overrode the default greeting.

## Test 2 — JVM System Property

Command:

```bash
java -Dpolar.greeting="Welcome to the catalog from JVM" \
  -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
```

Response:

```text
Welcome to the catalog from JVM
```

Result: The JVM system property successfully overrode the default greeting.

## Test 3 — Environment Variable

Command:

```bash
POLAR_GREETING="Welcome to the catalog from ENV" \
  java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
```

Response:

```text
Welcome to the catalog from ENV
```

Result: The environment variable successfully overrode the default greeting.

## Test 4 — Configuration Precedence

Command:

```bash
java -Dpolar.greeting="Welcome from JVM" \
  -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar \
  --polar.greeting="Welcome from CLI"
```

Response:

```text
Welcome from CLI
```

Result: The command-line argument took priority over the JVM system property.

## Conclusion

All four tests passed using the same packaged JAR without rebuilding it.

For the configuration sources tested in this practicum, Spring Boot applies the following priority order:

1. Command-line arguments
2. JVM system properties
3. Environment variables
4. Profile-specific configuration files
5. Default `application.yml`

This demonstrates how application configuration can change between environments without modifying or rebuilding the application.