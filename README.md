# Catalog Service

[![Commit Stage](https://github.com/p466-Pratik/catalog-service/actions/workflows/commit-stage.yml/badge.svg)](https://github.com/p466-Pratik/catalog-service/actions/workflows/commit-stage.yml)

Catalog Service is a Spring Boot REST API for managing books in the Polar Bookshop application.

## PUT ISBN Policy

For `PUT /books/{isbn}`, the ISBN in the URL path is authoritative.

The ISBN in the request body must match the path ISBN. If they differ, the API returns `400 Bad Request`.

This avoids ambiguity about which ISBN identifies the resource being updated.

For an existing book, PUT returns `200 OK`. If the book does not already exist, PUT creates it and returns `201 Created`.

## ISBN Validation

The application uses a custom `@ValidIsbn` validation constraint to verify ISBN check digits.

Both ISBN-10 and ISBN-13 values are supported. An ISBN must have both the correct format and a valid check digit.

## DELETE on a Missing Book

Returning `204 No Content` when deleting a book that does not exist is reasonable because DELETE is idempotent. After the request, the desired state is satisfied because the resource does not exist, whether it existed before the request or not. Returning the same result for repeated DELETE requests also keeps the API behavior predictable.

## Testing

The project includes:

- Book validation unit tests
- MVC controller slice tests
- JSON serialization and deserialization tests
- Spring Boot integration tests
- A full book lifecycle integration test covering POST, GET, PUT, DELETE, and a final GET returning `404 Not Found`

Run all tests with:

```bash
./gradlew test
```

Run the complete build with:

```bash
./gradlew clean build
```

## API Demonstration

Example API commands and responses are documented in:

```text
docs/api-demo.md
```

## Practicum 2 — E1: Publisher Schema Migration

The `publisher` field was added using the Flyway migration `V2__Add_publisher_column.sql`. The new column is initially nullable because existing books do not have a publisher value, and older versions of Catalog Service do not provide one. Making the column required immediately could break existing data and application instances.

**Two-release migration plan:**

1. **Release 1:** Add the nullable publisher column and update Catalog Service to support reading and writing publisher information. Existing application instances can continue working without providing a publisher.
2. **Release 2:** After all application instances have been updated and existing books have publisher values, add validation requiring a publisher and create a new Flyway migration that adds the `NOT NULL` constraint.

This approach allows the database schema and application to evolve without breaking older versions during deployment.