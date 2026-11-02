## PUT ISBN Policy

For `PUT /books/{isbn}`, the ISBN in the URL path is authoritative.
The ISBN in the request body must match the path ISBN. If they differ,
the API returns `400 Bad Request`.

This avoids ambiguity about which ISBN identifies the resource being updated.

## DELETE on a Missing Book

Returning `204 No Content` when deleting a book that does not exist is reasonable because DELETE is idempotent. After the request, the desired state is satisfied because the resource does not exist, whether it existed before the request or not. Returning the same result for repeated DELETE requests also keeps the API behavior predictable.