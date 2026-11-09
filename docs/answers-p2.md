# Practicum 2 — Bonus Questions

## 1. Why is it a mistake to name feature profiles dev and prod?

Using names like `dev` and `prod` makes features depend on specific environments. Naming profiles after what they do, like `testdata`, makes it easier to use the same feature in different environments.

## 2. Which polar.greeting value is used, and why are environment variables recommended?

The JVM property is used because it has higher priority than the environment variable and `application.yml`. Environment variables are still recommended because they make it easy to change settings without rebuilding the application.

## 3. What makes Catalog Service stateless, and why can three instances run behind a load balancer?

Catalog Service is stateless because it stores books in PostgreSQL instead of the application's memory. This allows three instances to use the same database and handle requests without having different book data.

## 4. Why test with PostgreSQL instead of H2?

PostgreSQL is used because it is the same database type the application uses in production. H2 might behave differently, so testing with PostgreSQL helps catch problems earlier.

## 5. Why should you never modify V1__Initial_schema.sql after it has been applied?

Flyway keeps track of migrations using checksums. Changing an old migration can cause an error because the checksum will not match. Instead, we should create a new migration for changes.

## 6. Why use JdbcAggregateTemplate instead of bookRepository.save() to insert test data?

`JdbcAggregateTemplate` lets us set up test data without using the repository method we are testing. This helps make sure the test checks the repository correctly instead of depending on its own methods.