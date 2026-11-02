## PUT ISBN Policy

For `PUT /books/{isbn}`, the ISBN in the URL path is authoritative.
The ISBN in the request body must match the path ISBN. If they differ,
the API returns `400 Bad Request`.

This avoids ambiguity about which ISBN identifies the resource being updated.