
# Practicum 3 — Containers

## Docker Layer Caching

I built Catalog Service using a multi-stage Dockerfile that extracts the Spring Boot JAR into separate layers.

### Initial Build

The first Docker build completed successfully and created the image `catalog-service:dockerfile`. Docker downloaded the Java 21 base image and created the application layers.

### Second Build

I modified `BookController.java`, rebuilt the JAR, and ran Docker build again.

The output showed:

- Dependencies: CACHED
- Spring Boot loader: CACHED
- Snapshot dependencies: CACHED
- Application: Rebuilt
- Non-root user setup: CACHED

This demonstrates that Docker can reuse unchanged dependency layers while rebuilding the application layer after a code change.

## Container Security

I verified the container user using:

`docker run --rm --entrypoint id catalog-service:dockerfile`

Output:

`uid=1001(spring) gid=1001(spring) groups=1001(spring)`

The container runs as the non-root user `spring`, following the principle of least privilege.


## Docker Compose and Database Persistence

I ran Catalog Service and PostgreSQL together using Docker Compose.

The Catalog Service container connected to PostgreSQL using the hostname `polar-postgres`, which Docker Compose resolves through its internal network.

I verified that `GET http://localhost:9001/books` returned HTTP 200 with two sample books.

I then ran:

```bash
docker compose down
docker compose up -d
```

After restarting, the two sample books appeared again with IDs starting at 1 and new creation timestamps.

This happens because PostgreSQL does not have a persistent volume configured. When Docker Compose removes the PostgreSQL container, its writable container layer is deleted, including the database records.

On restart, PostgreSQL initializes a fresh database, Flyway recreates the schema, and the `testdata` profile inserts the two sample books again.

In production, a persistent Docker volume or managed database would be needed to preserve data across container replacements.
